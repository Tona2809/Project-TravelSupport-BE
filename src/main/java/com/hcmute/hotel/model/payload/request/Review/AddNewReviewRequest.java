package com.hcmute.hotel.model.payload.request.Review;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@Setter
@Getter
public class AddNewReviewRequest {
    @NotEmpty(message = "tiêu đề không được để trống")
    private String title;
    @NotEmpty(message = "Nội dung không được để trống")
    private String content;
    @NotEmpty(message="place")
    private String place;
}
