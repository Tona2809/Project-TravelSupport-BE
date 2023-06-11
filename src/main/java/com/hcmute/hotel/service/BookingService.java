package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.BookingEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Component
public interface BookingService {

    BookingEntity addBooking(BookingEntity booking);
    boolean checkinValidate(String stayId, LocalDateTime checkinDate);
    boolean checkoutValidate(String stayId,LocalDateTime checkinDate,LocalDateTime checkoutDate);
    BookingEntity findBookingById(String id);
    boolean checkUserDateValidate(String userId,LocalDateTime checkinDate,LocalDateTime checkoutDate);
    List<BookingEntity> getUserBooking(String userId);
    List<BookingEntity> getBookingByStay(StayEntity stay);

    List<BookingEntity> getBookingByOwner(UserEntity userId);
}
