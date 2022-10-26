package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.ReviewEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.Review.AddNewReviewRequest;
import com.hcmute.hotel.model.payload.request.Review.UpdateReviewRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public interface ReviewService {
    ReviewEntity saveReview(AddNewReviewRequest addNewReviewRequest, UserEntity user);
    ReviewEntity updateReview(UpdateReviewRequest updateReviewRequest);
    public List<ReviewEntity> getAllReview();
    ReviewEntity getReviewById(String id);
    void deleteById(List<String> ListId);
}
