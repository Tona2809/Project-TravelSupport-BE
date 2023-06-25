package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.RoleEntity;
import com.hcmute.hotel.model.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<RoomEntity,String> {

    @Query(value = "select * from rooms where stay_id = ?",nativeQuery = true)
    List<RoomEntity> findAllByStayId(String stayid);
}
