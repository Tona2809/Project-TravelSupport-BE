package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.StayRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StayRatingRepository extends JpaRepository<StayRatingEntity,String> {
    @Modifying
    @Query(value =  "Delete from hotel_rating where id = ?", nativeQuery = true)
    void deleteById(String id);
    List<StayRatingEntity> findByStay(StayEntity stay);
}
