package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.RoomEntity;
import com.hcmute.hotel.model.entity.RoomServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomServiceRepository extends JpaRepository<RoomServiceEntity,String> {
    @Query(value = "select * from roomservice where room_id = ?",nativeQuery = true)
    List<RoomServiceEntity> findAllByRoomId(String roomid);
}
