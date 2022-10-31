package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.model.entity.BookingEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.Booking.AddNewBookingRequest;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.service.BookingService;
import com.hcmute.hotel.service.PaypalService;
import com.hcmute.hotel.service.StayService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.hcmute.hotel.controller.StayController.*;

@ComponentScan
@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {
    @Autowired
    AuthenticateHandler authenticateHandler;
    private final BookingService bookingService;
    private final StayService stayService;
    private final PaypalService paypalService;
    public static final String SUCCESS_URL = "/api/booking/pay/success";
    public static final String CANCEL_URL = "/api/order/pay/cancel";
    @PostMapping("")
    @ApiOperation("Create")
    public ResponseEntity<Object> addBooking(@RequestBody @Valid AddNewBookingRequest addNewBookingRequest, HttpServletRequest req)
    {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            BookingEntity booking = new BookingEntity();
            StayEntity stay = stayService.getStayById(addNewBookingRequest.getStayId());
            if (stay==null||user==stay.getHost())
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"STAY_NOT_FOUND_OR_OWNER","Can't find Stay with id provided or you are stay owner"),HttpStatus.NOT_FOUND);
            }
            booking.setCreateAt(LocalDateTime.now());
            booking.setUser(user);
            booking.setStay(stay);
            LocalDateTime checkinDate=addNewBookingRequest.getCheckinDate();
            LocalDateTime checkoutDate=addNewBookingRequest.getCheckoutDate();

            if (checkinDate.compareTo(checkoutDate)>0 || checkinDate.compareTo(stay.getTimeOpen())<0 || checkinDate.compareTo(stay.getTimeClose())>0||checkoutDate.compareTo(stay.getTimeOpen())<0 || checkoutDate.compareTo(stay.getTimeClose())>0)
            {
                return new ResponseEntity<>(new ErrorResponse(E400,"INVALID_CHECKIN_CHECKOUT_DATE","Invalid checkin or checkout date"),HttpStatus.NOT_FOUND);
            }
            if (!bookingService.checkinValidate(stay.getId(), addNewBookingRequest.getCheckinDate()))
            {
                return new ResponseEntity<>(new ErrorResponse(E400,"INVALID_CHECKIN_DATE","Invalid checkin date"),HttpStatus.BAD_REQUEST);

            }
            if (!bookingService.checkoutValidate(stay.getId(),addNewBookingRequest.getCheckinDate(),addNewBookingRequest.getCheckoutDate()))
            {
                return new ResponseEntity<>(new ErrorResponse(E400,"INVALID_CHECKOUT_DATE","Invalid checkout date"),HttpStatus.BAD_REQUEST);
            }
            if (!bookingService.checkUserDateValidate(user.getId(),checkinDate,checkoutDate))
            {
                return new ResponseEntity<>(new ErrorResponse(E400,"INVALID_USER_CHECKIN_CHECKOUT_DATE","Invalid user checkin or checkout date"),HttpStatus.NOT_FOUND);
            }
            booking.setCheckinDate(addNewBookingRequest.getCheckinDate());
            booking.setCheckoutDate(addNewBookingRequest.getCheckoutDate());
            Duration duration = Duration.between(addNewBookingRequest.getCheckinDate(),addNewBookingRequest.getCheckoutDate());
            int diff = (int) Math.abs(duration.toDays());
            int totalPrice=(diff+1)*stay.getPrice();
            booking.setTotalPrice(totalPrice);
            booking.setTotalPeople(addNewBookingRequest.getTotalPeople());
            booking.setCreateAt(LocalDateTime.now());
            booking.setStatus(0);
            booking=bookingService.addBooking(booking);
            String link= paypalPayment(booking,req);
            return new ResponseEntity<>(link,HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }
    private String paypalPayment(BookingEntity booking,HttpServletRequest req)
    {
        try {
            booking=bookingService.addBooking(booking);
            Payment paypalPayment = paypalService.createPayment(booking, "USD", "paypal",
                    "sale", req.getHeader("origin") + CANCEL_URL+"/"+booking.getId(),
                    req.getHeader("origin")+ SUCCESS_URL +"/"+booking.getId());
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
}
