package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.BookingEntity;
import com.hcmute.hotel.model.entity.RoomEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.repository.BookingRepository;
import com.hcmute.hotel.service.BookingService;
import com.paypal.api.payments.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    public boolean checkinValidate(String stayId, LocalDateTime checkinDate, String userId) {
        List<BookingEntity> list = bookingRepository.checkinValidate(stayId,checkinDate,userId);
        return list.isEmpty();
    }

    @Override
    public boolean checkoutValidate(String stayId, LocalDateTime checkinDate, LocalDateTime checkoutDate, String userId) {
        List<BookingEntity> list = bookingRepository.checkoutValidate(stayId,checkinDate,checkoutDate, userId);
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

    @Override
    public List<BookingEntity> getUserBooking(String userId) {
        List<BookingEntity> list = bookingRepository.getUserBooking(userId);
        return list;
    }

    @Override
    public List<BookingEntity> getBookingByStay(String stay) {
        List<BookingEntity> list = bookingRepository.getAllByStay(stay);
        return list;
    }

    @Override
    public void setCompletedBooking(String bookingId) {
        BookingEntity booking = findBookingById(bookingId);
        if (booking!=null && booking.getStatus()==4)
        {
            booking.setStatus(5);
            bookingRepository.save(booking);
        }
    }

    @Override
    public List<BookingEntity> getBookingByOwner(UserEntity userId, String searchKey) {
        List<BookingEntity> list = bookingRepository.getAllByStay_Host(userId.getId(),searchKey);
        return list;
    }

    @Override
    public BookingEntity getByPaymentId(String paymentId) {
        Optional<BookingEntity> bookingEntity = bookingRepository.getByPaymentId(paymentId);
        return bookingEntity.isEmpty() ? null : bookingEntity.get();
    }

    @Override
    public void deleteBookingById(String bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Override
    public Map<LocalDate, Integer> getOwnerMonthlyRevenue(String hostName) {
        List<Object> objects = bookingRepository.getRevenueLastMonth(hostName);
        Map<LocalDate, Integer> monthMap = new LinkedHashMap<>();

        for (Object obj : objects) {
            Object[] row = (Object[]) obj;
            BigDecimal countBigDecimal = (BigDecimal) row[1];
            if (countBigDecimal == null)
            {
                countBigDecimal= BigDecimal.valueOf(0);
            }
            Date roomId = (Date) row[0];
            String date = roomId.toString();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(date, formatter);
            Integer count = countBigDecimal.intValue();

            monthMap.put(localDate, count);
        }
        return monthMap;
    }

    @Override
    public Map<String, Integer> getStayRevenueByOwner(String userId) {
        List<Object> objects = bookingRepository.getRevenueOfStayByOwner(userId);
        Map<String, Integer> result = new HashMap<>();

        for (Object obj : objects) {
            Object[] row = (Object[]) obj;
            BigDecimal countBigDecimal = (BigDecimal) row[1];
            if (countBigDecimal == null)
            {
                countBigDecimal= BigDecimal.valueOf(0);
            }
            String roomName = (String) row[0];
            Integer count = countBigDecimal.intValue();

            result.put(roomName, count);
        }
        return result;
    }
}
