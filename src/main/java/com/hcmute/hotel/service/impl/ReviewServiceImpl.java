package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.ReviewEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.Review.AddNewReviewRequest;
import com.hcmute.hotel.model.payload.request.Review.UpdateReviewRequest;
import com.hcmute.hotel.repository.ReviewRepository;
import com.hcmute.hotel.repository.UserRepository;
import com.hcmute.hotel.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    @Override
    public ReviewEntity saveReview(AddNewReviewRequest addNewReviewRequest, UserEntity user) {
        ReviewEntity review =new ReviewEntity();
        review.setContent(addNewReviewRequest.getContent());
        review.setTitle(addNewReviewRequest.getTitle());
        review.setPlace(addNewReviewRequest.getPlace());
        review.setUserReview(userRepository.save(user));
        review.setLikeCount(0);
        review.setHidden(false);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        review.setVisitedTime(LocalDateTime.now());
        return reviewRepository.save(review);
    }
    public ReviewEntity updateReview(UpdateReviewRequest updateReviewRequest) {
        ReviewEntity review =getReviewById(updateReviewRequest.getId());
        review.setContent(updateReviewRequest.getContent());
        review.setTitle(updateReviewRequest.getTitle());
        review.setUpdatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    @Override
    public List<ReviewEntity> getAllReview() {
        List<ReviewEntity>listLecturer =reviewRepository.findAll();
        return listLecturer;
    }

    @Override
    public ReviewEntity getReviewById(String  id) {
        Optional<ReviewEntity> review =reviewRepository.findById(id);
        if(review.isEmpty())
            return null;
        return  review.get();
    }

    @Override
    public void deleteById(List<String> ListId) {
        for(String id:ListId){
            reviewRepository.deleteById(id);
        }
    }

}
