package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.RoleEntity;
import com.hcmute.hotel.model.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<RoomEntity,String> {
    List<RoomEntity> findAllByStayId(String stayId);
    @Query(value = "SELECT * FROM rooms where id in ?1",nativeQuery = true)
    List<RoomEntity> findByListId(List<String> listId);
}
