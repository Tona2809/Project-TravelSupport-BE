package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.RoleEntity;
import com.hcmute.hotel.model.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<RoomEntity,String> {
}
