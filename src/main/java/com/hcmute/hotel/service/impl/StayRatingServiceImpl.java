package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.StayRatingEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.StayRating.AddNewStayRatingRequest;
import com.hcmute.hotel.model.payload.request.StayRating.UpdateStayRatingRequest;
import com.hcmute.hotel.repository.StayRatingRepository;
import com.hcmute.hotel.repository.UserRepository;
import com.hcmute.hotel.service.StayRatingService;
import com.hcmute.hotel.service.StayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StayRatingServiceImpl implements StayRatingService {
    private final StayRatingRepository stayRatingRepository;
    private final StayService stayService;
    @Override
    public StayRatingEntity saveStayRating(AddNewStayRatingRequest addNewStayRatingRequest, UserEntity user) {
        StayRatingEntity stayRating =new StayRatingEntity();
        stayRating.setMessage(addNewStayRatingRequest.getMessage());
        stayRating.setStay(stayService.getStayById(addNewStayRatingRequest.getStayid()));
        stayRating.setRate(addNewStayRatingRequest.getRate());
        stayRating.setCreated_at(LocalDateTime.now(ZoneId.of("GMT+07:00")));
        stayRating.setUserRating(user);
        return stayRatingRepository.save(stayRating);
    }

    @Override
    public StayRatingEntity updateStayRating(UpdateStayRatingRequest updateStayRatingRequest) {
        StayRatingEntity stayRating =getStayRatingById(updateStayRatingRequest.getId());
        stayRating.setMessage(updateStayRatingRequest.getMessage());
        stayRating.setRate(updateStayRatingRequest.getRate());
        return stayRatingRepository.save(stayRating);
    }

    @Override
    public List<StayRatingEntity> getAllStayRating() {
        List<StayRatingEntity>listStayRating =stayRatingRepository.findAll();
        return listStayRating;
    }

    @Override
    public StayRatingEntity getStayRatingById(String id) {
        Optional<StayRatingEntity> stayRating =stayRatingRepository.findById(id);
        if(stayRating.isEmpty())
            return null;
        return  stayRating.get();
    }

    @Override
    public void deleteById(String id) {
        stayRatingRepository.deleteById(id);
    }

    @Override
    public List<StayRatingEntity> getStayRatingByStayId(String id) {
        StayEntity stay =stayService.getStayById(id);
        List<StayRatingEntity> listStayRating =stayRatingRepository.findByStay(stay);
        return listStayRating;
    }
}
