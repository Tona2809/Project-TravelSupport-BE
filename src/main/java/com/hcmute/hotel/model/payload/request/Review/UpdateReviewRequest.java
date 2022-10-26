package com.hcmute.hotel.model.payload.request.Review;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@Setter
@Getter
public class UpdateReviewRequest {
    @NotEmpty(message = "Id không được để trống")
    private String id;
    @NotEmpty(message = "Tiêu đề không được để trống")
    private String title;
    @NotEmpty(message = "Nội dung không được để trống")
    private String content;
}