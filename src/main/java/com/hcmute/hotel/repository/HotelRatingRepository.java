package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.HotelRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface HotelRatingRepository extends JpaRepository<HotelRatingEntity,String> {
    @Modifying
    @Query(value =  "Delete from hotel_rating where id = ?", nativeQuery = true)
    void deleteById(String id);
}
