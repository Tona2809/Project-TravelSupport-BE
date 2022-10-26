package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.HotelRatingEntity;
import com.hcmute.hotel.model.entity.ReviewEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.HotelRating.AddNewHotelRatingRequest;
import com.hcmute.hotel.model.payload.request.HotelRating.UpdateHotelRatingRequest;
import com.hcmute.hotel.repository.HotelRatingRepository;
import com.hcmute.hotel.repository.UserRepository;
import com.hcmute.hotel.service.HotelRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class HotelRatingServiceImpl implements HotelRatingService {
    private final UserRepository userRepository;
    private final HotelRatingRepository hotelRatingRepository;
    @Override
    public HotelRatingEntity saveHotelRating(AddNewHotelRatingRequest addNewHotelRatingRequest, UserEntity user) {
        HotelRatingEntity hotelRating =new HotelRatingEntity();
        hotelRating.setMessage(addNewHotelRatingRequest.getMessage());
        hotelRating.setHotel(addNewHotelRatingRequest.getHotel());
        hotelRating.setRate(addNewHotelRatingRequest.getRate());
        hotelRating.setCreated_at(LocalDateTime.now());
        hotelRating.setUserRating(user);
        return hotelRatingRepository.save(hotelRating);
    }

    @Override
    public HotelRatingEntity updateHotelRating(UpdateHotelRatingRequest updateHotelRatingRequest) {
        HotelRatingEntity hotelRating =getHotelRatingById(updateHotelRatingRequest.getId());
        hotelRating.setMessage(updateHotelRatingRequest.getMessage());
        hotelRating.setRate(updateHotelRatingRequest.getRate());
        return hotelRatingRepository.save(hotelRating);
    }

    @Override
    public List<HotelRatingEntity> getAllHotelRating() {
        List<HotelRatingEntity>listHotelRating =hotelRatingRepository.findAll();
        return listHotelRating;
    }

    @Override
    public HotelRatingEntity getHotelRatingById(String id) {
        Optional<HotelRatingEntity> hotelRating =hotelRatingRepository.findById(id);
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
