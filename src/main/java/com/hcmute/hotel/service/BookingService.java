package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.BookingEntity;
import com.hcmute.hotel.model.entity.RoomEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Component
public interface BookingService {

    BookingEntity addBooking(BookingEntity booking);
    boolean checkinValidate(String stayId, LocalDateTime checkinDate, String userId);
    boolean checkoutValidate(String stayId,LocalDateTime checkinDate,LocalDateTime checkoutDate, String userId);
    BookingEntity findBookingById(String id);
    boolean checkUserDateValidate(String userId,LocalDateTime checkinDate,LocalDateTime checkoutDate);
    List<BookingEntity> getUserBooking(String userId);
    List<BookingEntity> getBookingByStay(String stayId);
    void setCompletedBooking(String bookingId);
    List<BookingEntity> getBookingByOwner(UserEntity userId, String searchKey);

    BookingEntity getByPaymentId(String paymentId);

    void deleteBookingById(String bookingId);

    Map<LocalDate, Integer> getOwnerMonthlyRevenue(String hostName);

    Map<String, Integer> getStayRevenueByOwner(String userId);
}
