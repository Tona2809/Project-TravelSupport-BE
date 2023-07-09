package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.RoleEntity;
import com.hcmute.hotel.model.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomRepository extends JpaRepository<RoomEntity,String> {
    List<RoomEntity> findAllByStayId(String stayId);
    @Query(value = "SELECT * FROM rooms where id in ?1",nativeQuery = true)
    List<RoomEntity> findByListId(List<String> listId);

    @Query(value = "SELECT\n" +
            "    r.id, subquery.count\n" +
            "FROM\n" +
            "    rooms r\n" +
            "LEFT JOIN (\n" +
            "    SELECT\n" +
            "        r.id AS room_id, SUM(br.quantity) AS count\n" +
            "    FROM\n" +
            "        rooms r\n" +
            "    LEFT JOIN\n" +
            "        booking_rooms br ON r.id = br.room_id\n" +
            "    LEFT JOIN\n" +
            "        bookings b ON br.booking_id = b.id\n" +
            "    WHERE\n" +
            "        (?1 IS NOT NULL AND ?2 IS NOT NULL AND ?1 <= b.checkout_date AND ?2 >= b.checkin_date) AND r.stay_id = ?3 AND b.status NOT IN (-1, 0, 3, 4,5)\n" +
            "    GROUP BY\n" +
            "        r.id\n" +
            ") AS subquery ON r.id = subquery.room_id\n" +
            "WHERE\n" +
            "    r.stay_id = ?3", nativeQuery = true)
    List<Object> getCurrentAvailableRoom(LocalDateTime checkinDate, LocalDateTime checkoutDate,String stayId);
    @Query(value = "SELECT \n" +
            "    r.id, subquery.count\n" +
            "FROM \n" +
            "    rooms r\n" +
            "LEFT JOIN (\n" +
            "    SELECT \n" +
            "        r.id AS room_id, SUM(br.quantity) AS count\n" +
            "    FROM \n" +
            "        rooms r\n" +
            "    LEFT JOIN \n" +
            "        booking_rooms br ON r.id = br.room_id\n" +
            "    LEFT JOIN \n" +
            "        bookings b ON br.booking_id = b.id\n" +
            "    WHERE \n" +
            "        (?1 IS NOT NULL AND ?2 IS NOT NULL AND ?1 <= b.checkout_date and ?2 >=b.checkin_date) and r.stay_id =?4 and b.status NOT IN (-1,0,3,4,5)\n" +
            "    GROUP BY \n" +
            "        r.id\n" +
            ") AS subquery ON r.id = subquery.room_id\n" +
            "WHERE \n" +
            "    r.stay_id = ?4  and (?3 = 0 OR (r.guest_number between ?3 - ?5 AND ?3 + ?5) )", nativeQuery = true)
    List<Object> getAvailableRoom(LocalDateTime checkinDate, LocalDateTime checkoutDate, int Guest, String stayId, int flexibleNumbers);
}
