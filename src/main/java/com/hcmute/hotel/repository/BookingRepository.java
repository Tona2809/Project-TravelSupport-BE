package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.BookingEntity;
import com.hcmute.hotel.model.entity.RoomEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookingEntity,String> {

    @Query(value = "Select * from bookings where stay_id =?1 and ?2 between checkin_date and checkout_date and status <= 2 and user_id=?3 ",nativeQuery = true)
    List<BookingEntity> checkinValidate(String stayId,LocalDateTime date, String userId);
    @Query(value = "select * from bookings where stay_id=?1 and (checkin_date between ?2 and ?3) and status<=2 and user_id=?4",nativeQuery = true)
    List<BookingEntity> checkoutValidate(String stayId,LocalDateTime checkin,LocalDateTime checkout, String userId);
    @Query(value = "select * from bookings where user_id=?1 and ((checkin_date between ?2 and ?3) || (checkout_date between ?2 and?3))",nativeQuery = true)
    List<BookingEntity> checkUserDateValidate(String userId,LocalDateTime checkin,LocalDateTime checkout);
    @Query(value = "Select * from bookings where user_id=?1 order by create_at desc",nativeQuery = true)
    List<BookingEntity> getUserBooking(String userId);

    @Query(value = "Select * from bookings where user_id=?1 and status=1 order by create_at desc",nativeQuery = true)
    List<BookingEntity> getAllByStay(String stayId);

    @Query(value= "Select * from bookings inner join stays on bookings.stay_id=stays.id where host=?1 and bookings.status>0 and (CONCAT(bookings.id, ' ',stays.name) LIKE CONCAT('%', COALESCE(?2, ''), '%')) order by bookings.checkin_date", nativeQuery = true)
    List<BookingEntity> getAllByStay_Host(String user, String searchKey);

    Optional<BookingEntity> getByPaymentId(String paymentId);
}
