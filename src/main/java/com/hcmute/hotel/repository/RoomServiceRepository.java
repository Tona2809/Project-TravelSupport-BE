package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.RoomEntity;
import com.hcmute.hotel.model.entity.RoomServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomServiceRepository extends JpaRepository<RoomServiceEntity,String> {
    @Query(value = "select rs.* from service_for_room sfr inner join room_service rs on sfr.service_id = rs.id where sfr.room_id = ?",nativeQuery = true)
    List<RoomServiceEntity> findAllByRoomId(String roomid);

    Optional<RoomServiceEntity> findByRoomServiceName(String name);
}
