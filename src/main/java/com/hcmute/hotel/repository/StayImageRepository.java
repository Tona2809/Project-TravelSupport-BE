package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.StayImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StayImageRepository extends JpaRepository<StayImageEntity,String> {
    Optional<StayImageEntity> getByImgLink(String link);
}
