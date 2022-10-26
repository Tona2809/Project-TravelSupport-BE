package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.StayRatingEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.HotelRating.AddNewHotelRatingRequest;
import com.hcmute.hotel.model.payload.request.HotelRating.UpdateHotelRatingRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public interface StayRatingService {
    StayRatingEntity saveHotelRating(AddNewHotelRatingRequest addNewHotelRatingRequest, UserEntity user);
    StayRatingEntity updateHotelRating(UpdateHotelRatingRequest updateHotelRatingRequest);
    public List<StayRatingEntity> getAllHotelRating();
    StayRatingEntity getHotelRatingById(String id);
    void deleteById(List<String> ListId);
}
