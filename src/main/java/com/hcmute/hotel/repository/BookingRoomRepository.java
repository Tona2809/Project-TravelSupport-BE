package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.BookingRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRoomRepository extends JpaRepository<BookingRoomEntity,String> {

    List<BookingRoomEntity> getAllByBookingId(String bookingId);

}
