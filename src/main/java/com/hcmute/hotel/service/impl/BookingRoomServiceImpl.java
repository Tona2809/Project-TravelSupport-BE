package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.BookingRoomEntity;
import com.hcmute.hotel.repository.BookingRoomRepository;
import com.hcmute.hotel.service.BookingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingRoomServiceImpl implements BookingRoomService {
    private final BookingRoomRepository bookingRoomRepository;

    @Override
    public BookingRoomEntity addBookingRoom(BookingRoomEntity bookingRoom) {
        return bookingRoomRepository.save(bookingRoom);
    }

    @Override
    public List<BookingRoomEntity> getByBookingId(String bookingId) {
        return bookingRoomRepository.getAllByBookingId(bookingId);
    }

    @Override
    public BookingRoomEntity getById(String bookingRoomId) {
        Optional<BookingRoomEntity> bookingRoom = bookingRoomRepository.findById(bookingRoomId);
        return bookingRoom.isEmpty() ? null : bookingRoom.get();
    }

    @Override
    public List<BookingRoomEntity> getAllByRoomId(String roomId) {
        return bookingRoomRepository.getAllByRoomId(roomId);
    }
}
