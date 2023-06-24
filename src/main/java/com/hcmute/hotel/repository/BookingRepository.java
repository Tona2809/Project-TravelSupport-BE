package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.BookingEntity;
import com.hcmute.hotel.model.entity.RoomEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity,String> {

    @Query(value = "Select * from bookings where stay_id =?1 and ?2 between checkin_date and checkout_date and status=1",nativeQuery = true)
    List<BookingEntity> checkinValidate(String stayId,LocalDateTime date);
    @Query(value = "select * from bookings where stay_id=?1 and ((checkin_date between ?2 and ?3) || (checkout_date between ?2 and?3)) and status=1",nativeQuery = true)
    List<BookingEntity> checkoutValidate(String stayId,LocalDateTime checkin,LocalDateTime checkout);
    @Query(value = "select * from bookings where user_id=?1 and ((checkin_date between ?2 and ?3) || (checkout_date between ?2 and?3)) and status=1",nativeQuery = true)
    List<BookingEntity> checkUserDateValidate(String userId,LocalDateTime checkin,LocalDateTime checkout);
    @Query(value = "Select * from bookings where user_id=?1 and status=1 order by create_at asc",nativeQuery = true)
    List<BookingEntity> getUserBooking(String userId);
    List<BookingEntity> getAllByRoom_Stay_HostOrderByCheckinDateDesc(UserEntity user);
    List<BookingEntity> getAllByRoom(RoomEntity room);

    @Query(value = "Select * from bookings where user_id=?1 and status=1 order by create_at asc",nativeQuery = true)
    List<BookingEntity> getAllByStay(String stayId);
}
