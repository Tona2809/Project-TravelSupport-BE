package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.StayRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StayRatingRepository extends JpaRepository<StayRatingEntity,String> {
    @Modifying
    @Query(value =  "Delete from stay_rating where id = ?", nativeQuery = true)
    void deleteById(String id);

    @Query(value = "SELECT * From stay_rating order by created_at desc",nativeQuery = true)
    List<StayRatingEntity> getAllStayRating();
    List<StayRatingEntity> findByStay(StayEntity stay);

    @Query(value = "SELECT sr.* From stay_rating sr inner join stays s on sr.stay_id=s.id where sr.created_at >= DATE_SUB(CURRENT_DATE(), INTERVAL 90 DAY) and s.host = ?1 and sr.stay_id=?2 order by stay_id, sr.created_at desc",nativeQuery = true)
    List<StayRatingEntity> searchRating(String userId, String stayId);
}
