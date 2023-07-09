package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.StayRatingEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.StayRating.AddNewStayRatingRequest;
import com.hcmute.hotel.model.payload.request.StayRating.UpdateStayRatingRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public interface StayRatingService {
    StayRatingEntity saveStayRating(StayRatingEntity stayRating);
    StayRatingEntity updateStayRating(UpdateStayRatingRequest updateStayRatingRequest);
    public List<StayRatingEntity> getAllStayRating();
    StayRatingEntity getStayRatingById(String id);
    void deleteById(String id);
    public List<StayRatingEntity> getStayRatingByStayId(String id);
}
