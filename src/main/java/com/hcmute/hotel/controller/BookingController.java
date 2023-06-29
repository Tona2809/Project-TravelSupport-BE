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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
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
    public static final String SUCCESS_URL = "/api/booking/pay/success";
    public static final String CANCEL_URL = "/api/order/pay/cancel";
    @PostMapping("")
    @ApiOperation("Create")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> addBooking(@RequestBody @Valid AddNewBookingRequest addNewBookingRequest, HttpServletRequest req)
    {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = stayService.getStayById(addNewBookingRequest.getStayId());
            if (stay == null) {
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
            if (!bookingService.checkinValidate(stay.getId(), addNewBookingRequest.getCheckinDate())) {
                return new ResponseEntity<>(new ErrorResponse(E400, "INVALID_CHECKIN_DATE", "Invalid checkin date"), HttpStatus.BAD_REQUEST);

            }
            if (!bookingService.checkoutValidate(stay.getId(), addNewBookingRequest.getCheckinDate(), addNewBookingRequest.getCheckoutDate())) {
                return new ResponseEntity<>(new ErrorResponse(E400, "INVALID_CHECKOUT_DATE", "Invalid checkout date"), HttpStatus.BAD_REQUEST);
            }
            if (!bookingService.checkUserDateValidate(user.getId(), checkinDate, checkoutDate)) {
                return new ResponseEntity<>(new ErrorResponse(E400, "INVALID_USER_CHECKIN_CHECKOUT_DATE", "Invalid user checkin or checkout date"), HttpStatus.NOT_FOUND);
            }
            for (Map.Entry<String, Integer> entry : roomList.entrySet()) {
                RoomEntity room = roomService.findRoomById(entry.getKey());
                if (room == null) {
                    return new ResponseEntity<>(new ErrorResponse(E404, "ROOM_NOT_FOUND", "Can't find Stay with id provided or you are stay owner"), HttpStatus.NOT_FOUND);
                }
                totalRoom += entry.getValue();
                totalPrice += room.getPrice();
            }
            booking.setCheckinDate(addNewBookingRequest.getCheckinDate());
            booking.setCheckoutDate(addNewBookingRequest.getCheckoutDate());
            Duration duration = Duration.between(addNewBookingRequest.getCheckinDate(), addNewBookingRequest.getCheckoutDate());
            int diff = (int) Math.abs(duration.toDays());
            VoucherEntity voucher = voucherService.getVoucherById(addNewBookingRequest.getVoucherId());
            booking.setCreateAt(LocalDateTime.now());
            booking.setUser(user);
            booking.setStay(stay);
            booking.setTotalRoom(totalRoom);
            if (voucher == null) {
                totalPrice = (diff) * totalPrice;
            } else {
                if (voucher.getRemainingQuantity() < voucher.getQuantity()) {
                    totalPrice = (diff) * totalPrice * (1 - (voucher.getDiscount() / 100));
                    voucher = voucherService.userVoucher(user, voucher);
                    voucher.setRemainingQuantity(voucher.getRemainingQuantity() + 1);
                    voucherService.addVoucher(voucher);
                } else {
                    return new ResponseEntity<>(new ErrorResponse(E400, "INVALID_REMAIN_QUANTITY", "Invalid Remain quantity must be more than 0"), HttpStatus.BAD_REQUEST);
                }
            }
            booking.setTotalPrice(totalPrice);
            booking.setTotalPeople(addNewBookingRequest.getTotalPeople());
            booking.setCreateAt(LocalDateTime.now());
            booking.setStatus(0);
                Map<String, Object> map = vnpayService.createPayment(booking, req);
            while (bookingService.getByPaymentId(map.get("paymentId").toString())!=null)
            {
                map = vnpayService.createPayment(booking, req);
            }
            booking.setPaymentId(map.get("paymentId").toString());
            bookingService.addBooking(booking);
            for (Map.Entry<String, Integer> entry : roomList.entrySet()) {
                BookingRoomEntity bookingRoom = new BookingRoomEntity();
                bookingRoom.setBooking(booking);
                bookingRoom.setRoom(roomService.findRoomById(entry.getKey()));
                bookingRoom.setQuantity(entry.getValue());
                bookingRoomService.addBookingRoom(bookingRoom);
            }
            return new ResponseEntity<>(map.get("link").toString(),HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        } catch (UnsupportedEncodingException e) {
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

    @GetMapping("/pay/complete")
    public ResponseEntity<Object> payCompleted(
            @RequestParam("vnp_TxnRef") String paymentId
    )
    {
        BookingEntity booking=bookingService.getByPaymentId(paymentId);
        if (booking==null)
        {
            return new ResponseEntity<>(new ErrorResponse(E400,"NO_BOOKING_FOUND","Booking failure"),HttpStatus.BAD_REQUEST);
        }
        booking.setPaymentId(null);
        booking.setStatus(1);
        booking = bookingService.addBooking(booking);
        return new ResponseEntity<>(booking, HttpStatus.OK);
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
    public ResponseEntity<Object> searchOwnerBooking(HttpServletRequest req)
    {
        UserEntity user;
        try
        {
            user=authenticateHandler.authenticateUser(req);
            List<BookingEntity> list = bookingService.getBookingByOwner(user);
            Map<String, Object> map = new HashMap<>();
            map.put("content", list);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/Owner/{bookingId}")
    @ApiOperation("Complete Booking")
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
            booking.setStatus(2);
            booking=bookingService.addBooking(booking);
            return new ResponseEntity<>(booking,HttpStatus.OK);
        }catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
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
            int totalBooking=0,totalComplete=0,totalEarning=0;
            user=authenticateHandler.authenticateUser(req);
            List<BookingEntity> list = bookingService.getBookingByOwner(user);
            for (BookingEntity booking : list)
            {
                if (booking.getStatus()==2)
                {
                    totalBooking+=1;
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

                // Exclude bookingEntity if checkoutDate is earlier than current LocalDateTime.now()
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
