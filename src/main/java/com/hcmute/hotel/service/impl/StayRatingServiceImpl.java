package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.StayRatingEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.HotelRating.AddNewHotelRatingRequest;
import com.hcmute.hotel.model.payload.request.HotelRating.UpdateHotelRatingRequest;
import com.hcmute.hotel.repository.StayRatingRepository;
import com.hcmute.hotel.repository.StayRepository;
import com.hcmute.hotel.repository.UserRepository;
import com.hcmute.hotel.service.StayRatingService;
import com.hcmute.hotel.service.StayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StayRatingServiceImpl implements StayRatingService {
    private final UserRepository userRepository;
    private final StayRatingRepository hotelRatingRepository;
    private final StayService stayService;
    @Override
    public StayRatingEntity saveHotelRating(AddNewHotelRatingRequest addNewHotelRatingRequest, UserEntity user) {
        StayRatingEntity hotelRating =new StayRatingEntity();
        hotelRating.setMessage(addNewHotelRatingRequest.getMessage());
        hotelRating.setStay(stayService.getStayById(addNewHotelRatingRequest.getHotel()));
        hotelRating.setRate(addNewHotelRatingRequest.getRate());
        hotelRating.setCreated_at(LocalDateTime.now());
        hotelRating.setUserRating(user);
        return hotelRatingRepository.save(hotelRating);
    }

    @Override
    public StayRatingEntity updateHotelRating(UpdateHotelRatingRequest updateHotelRatingRequest) {
        StayRatingEntity hotelRating =getHotelRatingById(updateHotelRatingRequest.getId());
        hotelRating.setMessage(updateHotelRatingRequest.getMessage());
        hotelRating.setRate(updateHotelRatingRequest.getRate());
        return hotelRatingRepository.save(hotelRating);
    }

    @Override
    public List<StayRatingEntity> getAllHotelRating() {
        List<StayRatingEntity>listHotelRating =hotelRatingRepository.findAll();
        return listHotelRating;
    }

    @Override
    public StayRatingEntity getHotelRatingById(String id) {
        Optional<StayRatingEntity> hotelRating =hotelRatingRepository.findById(id);
        if(hotelRating.isEmpty())
            return null;
        return  hotelRating.get();
    }

    @Override
    public void deleteById(List<String> ListId) {
        for (String id : ListId) {
            hotelRatingRepository.deleteById(id);
        }
    }
}
