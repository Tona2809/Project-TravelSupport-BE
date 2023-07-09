package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.PlaceImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceImageRepository extends JpaRepository<PlaceImageEntity, String> {
    Optional<PlaceImageEntity> getByImgLink(String imgLink);
}
