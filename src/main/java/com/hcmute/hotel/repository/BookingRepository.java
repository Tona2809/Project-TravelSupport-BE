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
    @Query(value = "SELECT\n" +
            "    s.name AS stay_name,\n" +
            "    SUM(b.total_price) AS total_price_sum\n" +
            "FROM\n" +
            "    stays s\n" +
            "    INNER JOIN bookings b ON s.id = b.stay_id\n" +
            "WHERE\n" +
            "    s.host = ?1\n" +
            "    AND b.status NOT IN (-1, 0, 3, 4)\n" +
            "    AND b.create_at >= DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY)\n" +
            "GROUP BY\n" +
            "    s.name\n" +
            "ORDER BY\n" +
            "    total_price_sum DESC;", nativeQuery = true)
    List<Object> getRevenueOfStayByOwner(String userId);

    Optional<BookingEntity> getByPaymentId(String paymentId);
    @Query(value = "SELECT\n" +
            "    dates.date,\n" +
            "    COALESCE(SUM(b.total_price), 0) AS total_price\n" +
            "FROM\n" +
            "    (\n" +
            "        SELECT DATE_SUB(CURRENT_DATE(), INTERVAL (t3.n * 100 + t2.n * 10 + t.n) DAY) AS date\n" +
            "        FROM\n" +
            "            (\n" +
            "                SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL\n" +
            "                SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9\n" +
            "            ) t,\n" +
            "            (\n" +
            "                SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL\n" +
            "                SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9\n" +
            "            ) t2,\n" +
            "            (\n" +
            "                SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL\n" +
            "                SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9\n" +
            "            ) t3\n" +
            "        WHERE\n" +
            "            DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY) <= DATE_SUB(CURRENT_DATE(), INTERVAL (t3.n * 100 + t2.n * 10 + t.n) DAY)\n" +
            "    ) dates\n" +
            "LEFT JOIN\n" +
            "    bookings b ON DATE(b.create_at) = dates.date\n" +
            "LEFT JOIN\n" +
            "    stays s ON b.stay_id = s.id AND s.host = ?1\n" +
            "WHERE\n" +
            "    b.status NOT IN (-1, 0, 3, 4)\n" +
            "GROUP BY\n" +
            "    dates.date\n" +
            "ORDER BY\n" +
            "    dates.date ASC", nativeQuery = true)
    List<Object> getRevenueLastMonth(String hostId);
}
