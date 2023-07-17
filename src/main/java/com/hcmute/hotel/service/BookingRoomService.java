package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.BookingRoomEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
public interface BookingRoomService {
    BookingRoomEntity addBookingRoom(BookingRoomEntity bookingRoom);
    List<BookingRoomEntity> getByBookingId(String bookingId);
    BookingRoomEntity getById(String bookingRoomId);
    List<BookingRoomEntity> getAllByRoomId(String roomId);
}
