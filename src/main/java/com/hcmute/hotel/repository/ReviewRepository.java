package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<ReviewEntity,String> {
    @Modifying
    @Query(value =  "Delete from review where id = ?", nativeQuery = true)
    void deleteById(String id);
}
