package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.BookingEntity;
import com.hcmute.hotel.repository.BookingRepository;
import com.hcmute.hotel.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    @Override
    public BookingEntity addBooking(BookingEntity booking) {
        return bookingRepository.save(booking);

    }

    @Override
    public boolean checkinValidate(String stayId, LocalDateTime checkinDate) {
        List<BookingEntity> list = bookingRepository.checkinValidate(stayId,checkinDate);
        return list.isEmpty();
    }

    @Override
    public boolean checkoutValidate(String stayId, LocalDateTime checkinDate, LocalDateTime checkoutDate) {
        List<BookingEntity> list = bookingRepository.checkoutValidate(stayId,checkinDate,checkoutDate);
        return list.isEmpty();
    }

    @Override
    public BookingEntity findBookingById(String id) {
        Optional<BookingEntity> bookingEntity = bookingRepository.findById(id);
        return bookingEntity.get();
    }

    @Override
    public boolean checkUserDateValidate(String userId, LocalDateTime checkinDate, LocalDateTime checkoutDate) {
        List<BookingEntity> list = bookingRepository.checkUserDateValidate(userId,checkinDate,checkoutDate);
        return list.isEmpty();
    }
}
