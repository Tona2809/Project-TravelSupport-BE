package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.AmenitiesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AmenitiesRepository extends JpaRepository<AmenitiesEntity,String> {
    @Query(value = "SELECT * FROM amenities where name = ?1",
    countQuery = "SELECT COUNT(*) FROM amenities where name = ?1",
    nativeQuery = true)
List<AmenitiesEntity> checkSameNameAmenities(String name);
    Optional<AmenitiesEntity> findByName(String name);
}
