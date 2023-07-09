package com.hcmute.hotel.controller;

import com.hcmute.hotel.common.BookingStatusEnum;
import com.hcmute.hotel.common.OrderByEnum;
import com.hcmute.hotel.common.StaySortEnum;
import com.hcmute.hotel.common.StayStatus;
import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.model.entity.*;
import com.hcmute.hotel.model.payload.request.Booking.AddNewBookingRequest;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.service.*;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hcmute.hotel.controller.StayController.*;

@ComponentScan
@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {
    @Autowired
    AuthenticateHandler authenticateHandler;

    private final BookingService bookingService;

    private final BookingRoomService bookingRoomService;
    private final VnpayService vnpayService;
    private final StayService stayService;

    private final RoomService roomService;
    private final PaypalService paypalService;

    private final VoucherService voucherService;
    private final EmailService emailService;
    public static final String SUCCESS_URL = "/api/booking/pay/success";
    public static final String CANCEL_URL = "/api/order/pay/cancel";
    @PostMapping("")
    @ApiOperation("Create")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional(rollbackFor = {Throwable.class, RuntimeException.class})
    public ResponseEntity<Object> addBooking(@RequestBody @Valid AddNewBookingRequest addNewBookingRequest, HttpServletRequest req)
    {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = stayService.getStayById(addNewBookingRequest.getStayId());
            if (stay == null || stay.getHost() == user) {
                return new ResponseEntity<>(new ErrorResponse(E404, "STAY_NOT_FOUND_OR_OWNER", "Can't find Stay with id provided or you are stay owner"), HttpStatus.NOT_FOUND);
            }
            BookingEntity booking = new BookingEntity();
            Map<String, Integer> roomList = addNewBookingRequest.getRoomList();
            LocalDateTime checkinDate = addNewBookingRequest.getCheckinDate();
            LocalDateTime checkoutDate = addNewBookingRequest.getCheckoutDate();
            int totalPrice = 0;
            int totalRoom = 0;

            if (checkinDate.compareTo(checkoutDate) > 0 || checkinDate.compareTo(stay.getTimeOpen()) < 0 || checkinDate.compareTo(stay.getTimeClose()) > 0 || checkoutDate.compareTo(stay.getTimeOpen()) < 0 || checkoutDate.compareTo(stay.getTimeClose()) > 0) {
                return new ResponseEntity<>(new ErrorResponse(E400, "INVALID_CHECKIN_CHECKOUT_DATE", "Invalid checkin or checkout date"), HttpStatus.NOT_FOUND);
            }
            if (!bookingService.checkinValidate(stay.getId(), addNewBookingRequest.getCheckinDate(),user.getId())) {
                return new ResponseEntity<>(new ErrorResponse(E400, "INVALID_CHECKIN_DATE", "Invalid checkin date"), HttpStatus.BAD_REQUEST);
            }
            if (!bookingService.checkoutValidate(stay.getId(), addNewBookingRequest.getCheckinDate(), addNewBookingRequest.getCheckoutDate(), user.getId())) {
                return new ResponseEntity<>(new ErrorResponse(E400, "INVALID_CHECKOUT_DATE", "Invalid checkout date"), HttpStatus.BAD_REQUEST);
            }
            if (!bookingService.checkUserDateValidate(user.getId(), checkinDate, checkoutDate)) {
                return new ResponseEntity<>(new ErrorResponse(E400, "INVALID_USER_CHECKIN_CHECKOUT_DATE", "Invalid user checkin or checkout date"), HttpStatus.NOT_FOUND);
            }


            Duration duration = Duration.between(addNewBookingRequest.getCheckinDate(), addNewBookingRequest.getCheckoutDate());
            int diff = (int) Math.abs(duration.toDays());
            List<VoucherEntity> voucherEntityList = new ArrayList<>();
            if (addNewBookingRequest.getVoucherId()[0]!=null) {
                for (String voucherId : addNewBookingRequest.getVoucherId()) {
                    VoucherEntity voucher = voucherService.getVoucherById(voucherId);
                    voucherEntityList.add(voucher);
                }
            }
            for (Map.Entry<String, Integer> entry : roomList.entrySet()) {
                double discount = 0;
                RoomEntity room = roomService.findRoomById(entry.getKey());
                if (room == null) {
                    return new ResponseEntity<>(new ErrorResponse(E404, "ROOM_NOT_FOUND", "Can't find Stay with id provided or you are stay owner"), HttpStatus.NOT_FOUND);
                }
                for (VoucherEntity voucher : voucherEntityList) {
                    if (voucher.getRoom().getId() == room.getId()) {
                        discount = (double) voucher.getDiscount() / 100;
                    }
                }
                totalRoom += entry.getValue();
                totalPrice += room.getPrice() * (1 - discount);
            }

            LocalDateTime now = LocalDateTime.now();
            LocalTime timeCheckin = LocalTime.parse(stay.getCheckinTime(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime timeCheckout = LocalTime.parse(stay.getCheckoutTime(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalDateTime checkinTime = addNewBookingRequest.getCheckinDate().with(timeCheckin);
            LocalDateTime checkoutTime = addNewBookingRequest.getCheckoutDate().with(timeCheckout);
            booking.setCheckinDate(checkinTime);
            booking.setCheckoutDate(checkoutTime);
            int timeDiff = (int) ChronoUnit.HOURS.between(now,checkinTime);
            LocalDateTime expiredPaymentDate;
            if (timeDiff<=4)
            {
                return new ResponseEntity<>(new ErrorResponse(E400,"TIME_ERROR","Ngày đặt phòng của bạn quá gần thời gian checkin"),HttpStatus.BAD_REQUEST);
            } else if (timeDiff<=24) {
                expiredPaymentDate= checkinTime.with(timeCheckin).minus(3, ChronoUnit.HOURS);
            } else if (timeDiff<=48) {
                expiredPaymentDate= checkinTime.with(timeCheckin).minus(12, ChronoUnit.HOURS);
            } else {
                expiredPaymentDate= checkinTime.with(timeCheckin).minus(1, ChronoUnit.DAYS);
            }
            booking.setExpiredPaymentTime(expiredPaymentDate);
            booking.setCreateAt(LocalDateTime.now());
            booking.setUser(user);
            booking.setStay(stay);
            booking.setTotalRoom(totalRoom);
            booking.setTotalPrice(totalPrice * diff);
            booking.setTotalPeople(addNewBookingRequest.getTotalPeople());
            booking.setCreateAt(LocalDateTime.now());
            booking.setStatus(0);
            Map<String, Integer> currentRoomQuantity = roomService.getCurrentAvailableRoom(checkinTime, checkoutTime, stay.getId());
                Map<String, Object> map = vnpayService.createPayment(booking, req);
            while (bookingService.getByPaymentId(map.get("paymentId").toString())!=null)
            {
                map = vnpayService.createPayment(booking, req);
            }
            booking.setPaymentId(map.get("paymentId").toString());
            bookingService.addBooking(booking);
            for (Map.Entry<String, Integer> entry : roomList.entrySet()) {
                BookingRoomEntity bookingRoom = new BookingRoomEntity();
                RoomEntity room = roomService.findRoomById(entry.getKey());
                int currentQuantity=0;
                if (currentRoomQuantity.get(room.getId())!=null)
                {
                    currentQuantity=currentRoomQuantity.get(room.getId());
                }
                if (entry.getValue() > room.getNumberOfRoom()-currentQuantity)
                {
                    throw new RuntimeException(room.getRoomName() + " đã hết. Vui lòng kiểm tra lại");
                }
                for (VoucherEntity voucher : voucherEntityList)
                {
                    if (voucher.getRoom().getId() == room.getId())
                    {
                        bookingRoom.setVoucher(voucher);
                        voucher.getUsers().add(user);
                    }
                }
                bookingRoom.setBooking(booking);
                bookingRoom.setRoom(room);
                bookingRoom.setQuantity(entry.getValue());
                bookingRoomService.addBookingRoom(bookingRoom);
            }
            return new ResponseEntity<>(map.get("link").toString(),HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        } catch (UnsupportedEncodingException | RuntimeException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(new ErrorResponse(E400, "ERROR", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    private String paypalPayment(BookingEntity booking,HttpServletRequest req)
    {
        try {
            booking=bookingService.addBooking(booking);
            Payment paypalPayment = paypalService.createPayment(booking, "USD", "paypal",
                    "sale", req.getHeader("origin") + CANCEL_URL+"/"+booking.getId(),
                    "http://localhost:3000"+ SUCCESS_URL +"/"+booking.getId());
            for (Links link : paypalPayment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return link.getHref();
                }
            }
        }
        catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return null;
    }
    @GetMapping("/pay/success/{id}")
    @ResponseBody
    public ResponseEntity<Object> paypalSuccess(@PathVariable("id") String id,@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            System.out.println(payment.toJSON());
            BookingEntity booking = bookingService.findBookingById(id);
            if (payment.getState().equals("approved")) {
                booking.setStatus(1);
                booking = bookingService.addBooking(booking);
                return new ResponseEntity<>(booking, HttpStatus.OK);
            }
        } catch (PayPalRESTException e) {
            return new ResponseEntity<>(new ErrorResponse(E400,"PAYPAL_EXCEPTION",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
      return new ResponseEntity<>(new ErrorResponse(E400,"BOOKING_FAIL","Booking failure"),HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/pay/cancel/{id}")
    public ResponseEntity<Object> paypalCancel(@PathVariable("id") int id) {
        return new ResponseEntity<>(new ErrorResponse(E400,"BOOKING_FAIL","Booking failure"),HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/repay")
    @ApiOperation("Repay payment")
    public ResponseEntity<Object> repay(HttpServletRequest req,
                                        @RequestParam("vnp_TxnRef") String paymentId) {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            BookingEntity booking = bookingService.getByPaymentId(paymentId);
            if (booking == null) {
                return new ResponseEntity<>(new ErrorResponse(E400, "NO_BOOKING_FOUND", "Booking failure"), HttpStatus.BAD_REQUEST);
            }
            if (LocalDateTime.now().compareTo(booking.getExpiredPaymentTime())>=0)
            {
                return new ResponseEntity<>(new ErrorResponse(E400, "BOOKING_EXPIRED", "Your booking is expired"), HttpStatus.BAD_REQUEST);
            }
            Map<String, Object> map = vnpayService.createPayment(booking, req, paymentId);
            return new ResponseEntity<>(map.get("link").toString(),HttpStatus.OK);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }


    @GetMapping("/pay/complete")
    @Transactional(rollbackFor = {Exception.class, Throwable.class, RuntimeException.class})
    public ResponseEntity<Object> payCompleted(
            @RequestParam("vnp_TxnRef") String paymentId
    )
    {
        try {
            BookingEntity booking = bookingService.getByPaymentId(paymentId);
            if (booking == null) {
                return new ResponseEntity<>(new ErrorResponse(E400, "NO_BOOKING_FOUND", "Booking failure"), HttpStatus.BAD_REQUEST);
            }
            booking.setPaymentId(null);
            booking.setStatus(1);
            booking.setExpiredConfirmTime(booking.getCheckinDate().minus(2, ChronoUnit.HOURS));
            Map<String, Integer> currentRoomQuantity = roomService.getCurrentAvailableRoom(booking.getCheckinDate(), booking.getCheckoutDate(), booking.getStay().getId());
            for (BookingRoomEntity bookingRoom : booking.getBookingRoom()) {
                int currentRooms = 0;
                if (currentRoomQuantity.get(bookingRoom.getRoom().getId())!=null)
                {
                    currentRooms=currentRoomQuantity.get(bookingRoom.getRoom().getId());
                }
                if (bookingRoom.getVoucher() != null) {
                    if (bookingRoom.getVoucher().getRemainingQuantity() + 1 > bookingRoom.getVoucher().getQuantity()) {
                       throw new RuntimeException("Voucher" + bookingRoom.getVoucher().getName() + "đã hết hạn hoặc không còn hợp lệ");
                    }
                    bookingRoom.getVoucher().setRemainingQuantity(bookingRoom.getVoucher().getRemainingQuantity() + 1);
                }
            }
            booking = bookingService.addBooking(booking);
            emailService.sendOwnerBookingConfirmation(booking, booking.getStay().getHost().getFullName());
            return new ResponseEntity<>(booking, HttpStatus.OK);
        }
        catch (MessagingException | UnsupportedEncodingException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(new ErrorResponse(E400, "ERROR", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("")
    @ApiOperation("Get User Booking")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> getUserBooking(HttpServletRequest req) {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            List<BookingEntity> list = bookingService.getUserBooking(user.getId());
            Map<String, Object> map = new HashMap<>();
            map.put("content", list);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/Owner")
    @ApiOperation("Get Booking by Owner")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> searchOwnerBooking(HttpServletRequest req,@RequestParam(value = "searchKey",required = false) String searchKey)
    {
        UserEntity user;
        try
        {
            user=authenticateHandler.authenticateUser(req);
            List<BookingEntity> list = bookingService.getBookingByOwner(user, searchKey==null ? "" : searchKey);
            Map<String, Object> map = new HashMap<>();
            map.put("content", list);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/Owner/decline/{bookingId}")
    @ApiOperation("Decline Booking")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    @Transactional(rollbackFor = {Throwable.class,RuntimeException.class})
    public ResponseEntity<Object> declineUserBooking(HttpServletRequest req,@PathVariable("bookingId") String bookingId, @RequestParam("reason") String reason)
    {
        UserEntity user;
        try
        {
            user = authenticateHandler.authenticateUser(req);
            BookingEntity booking = bookingService.findBookingById(bookingId);
            if (booking == null || booking.getStatus()==4)
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"BOOKING_NOT_FOUND","Can't get booking with id provided"),HttpStatus.NOT_FOUND);
            }
            for (BookingRoomEntity bookingRoom : booking.getBookingRoom())
            {
                VoucherEntity voucher = bookingRoom.getVoucher();
                if (voucher!=null)
                {
                    voucher.getUsers().remove(booking.getUser());
                }
            }
            booking.setStatus(3);
            booking = bookingService.addBooking(booking);
            emailService.sendUserDeclineBookingEmail(booking, reason);
            return new ResponseEntity<>(booking,HttpStatus.OK);
        }catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        } catch (MessagingException | UnsupportedEncodingException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(new ErrorResponse(E400, "ERROR", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/decline/{bookingId}")
    @ApiOperation("User Decline Booking")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> declineBooking(HttpServletRequest req,@PathVariable("bookingId") String bookingId)
    {
        UserEntity user;
        try
        {
            user = authenticateHandler.authenticateUser(req);
            BookingEntity booking = bookingService.findBookingById(bookingId);
            if (booking == null || booking.getStatus()== 3)
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"BOOKING_NOT_FOUND","Can't get booking with id provided"),HttpStatus.NOT_FOUND);
            }
            for (BookingRoomEntity bookingRoom : booking.getBookingRoom())
            {
                VoucherEntity voucher = bookingRoom.getVoucher();
                if (voucher!=null)
                {
                    voucher.getUsers().remove(user);
                }
            }
            booking.setStatus(4);
            booking = bookingService.addBooking(booking);
            return new ResponseEntity<>(booking,HttpStatus.OK);
        }catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/Owner/{bookingId}")
    @ApiOperation("Confirm Booking")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ResponseEntity<Object> completeBooking(HttpServletRequest req,@PathVariable("bookingId") String bookingId)
    {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            BookingEntity booking = bookingService.findBookingById(bookingId);

            if (booking == null)
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"BOOKING_NOT_FOUND","Can't get booking with id provided"),HttpStatus.NOT_FOUND);
            }
            Map<String, Integer> currentRoomQuantity = roomService.getCurrentAvailableRoom(booking.getCheckinDate(), booking.getCheckoutDate(), booking.getStay().getId());
            for (BookingRoomEntity bookingRoom : booking.getBookingRoom()) {
                int currentRooms = bookingRoom.getRoom().getNumberOfRoom();
                if (currentRoomQuantity.get(bookingRoom.getRoom().getId()) != null) {
                    currentRooms = currentRoomQuantity.get(bookingRoom.getRoom().getId());
                }
                if (bookingRoom.getQuantity()> currentRooms) {
                    throw new RuntimeException("Phòng " + bookingRoom.getRoom().getRoomName() +" đã hết");
                }
            }
            booking.setStatus(2);
            booking=bookingService.addBooking(booking);
            return new ResponseEntity<>(booking,HttpStatus.OK);
        }catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
        catch (RuntimeException e)
        {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<>(new ErrorResponse(E400, "ERROR", e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping("/getTotalEarn")
    @ApiOperation("Get Owner Earning")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> searchOwnerStatic(HttpServletRequest req)
    {
        UserEntity user;
        try
        {
            int totalComplete=0,totalEarning=0;
            user=authenticateHandler.authenticateUser(req);
            List<BookingEntity> list = bookingService.getBookingByOwner(user,"");
            for (BookingEntity booking : list)
            {
                if (booking.getStatus()==5)
                {
                    totalComplete+=1;
                    totalEarning+=booking.getTotalPrice();
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("totalBooking", list.size());
            map.put("totalComplete", totalComplete);
            map.put("totalEarning",totalEarning);
            map.put("Host",user);

            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/getBlockedDate/{stayId}")
    @ApiOperation("Get Date unavailable")
    public ResponseEntity<Object> getDateBlocked(@PathVariable("stayId") String stayId)
    {
        StayEntity stay = stayService.getStayById(stayId);
        if (stay == null)
        {
            return new ResponseEntity<>(new ErrorResponse(E404,"STAY_NOT_FOUND","Can't get room with id provided"),HttpStatus.NOT_FOUND);
        }
        List<BookingEntity> bookingEntities = bookingService.getBookingByStay(stayId);
        List<LocalDateTime> responseList = new ArrayList<>();
        if (bookingEntities != null)
        {
            for (BookingEntity bookingEntity : bookingEntities) {
                LocalDateTime checkin = bookingEntity.getCheckinDate();
                LocalDateTime checkout = bookingEntity.getCheckoutDate();

                if (checkout.isBefore(LocalDateTime.now())) {
                    continue;
                }
                LocalDateTime current = checkin;
                while (current.isBefore(checkout)) {
                    responseList.add(current);
                    current = current.plusDays(1);
                }
            }
        }
        return new ResponseEntity<>(responseList,HttpStatus.OK);
    }

}
