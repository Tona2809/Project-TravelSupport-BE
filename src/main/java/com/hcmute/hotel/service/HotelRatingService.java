package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.HotelRatingEntity;
import com.hcmute.hotel.model.entity.ReviewEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.HotelRating.AddNewHotelRatingRequest;
import com.hcmute.hotel.model.payload.request.HotelRating.UpdateHotelRatingRequest;
import com.hcmute.hotel.model.payload.request.Review.AddNewReviewRequest;
import com.hcmute.hotel.model.payload.request.Review.UpdateReviewRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public interface HotelRatingService {
    HotelRatingEntity saveHotelRating(AddNewHotelRatingRequest addNewHotelRatingRequest, UserEntity user);
    HotelRatingEntity updateHotelRating(UpdateHotelRatingRequest updateHotelRatingRequest);
    public List<HotelRatingEntity> getAllHotelRating();
    HotelRatingEntity getHotelRatingById(String id);
    void deleteById(List<String> ListId);
}
