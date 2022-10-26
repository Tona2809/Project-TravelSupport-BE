package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StayRepository extends JpaRepository<StayEntity,String> {
    List<StayEntity> findAllByHost(UserEntity user);
}
