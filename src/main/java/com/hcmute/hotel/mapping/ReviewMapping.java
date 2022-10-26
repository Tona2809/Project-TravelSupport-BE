package com.hcmute.hotel.mapping;

import com.hcmute.hotel.model.entity.ReviewEntity;
import com.hcmute.hotel.model.payload.request.Review.AddNewReviewRequest;

public class ReviewMapping {
    public static ReviewEntity reviewToEntity(AddNewReviewRequest addNewReviewRequest){
        ReviewEntity review =new ReviewEntity();
        review.setContent(addNewReviewRequest.getContent());
        review.setTitle(addNewReviewRequest.getTitle());
        review.setPlace(addNewReviewRequest.getPlace());
        return review;
    }
}
