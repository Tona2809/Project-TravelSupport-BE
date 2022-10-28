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
    @NotEmpty(message = "Title name can not be empty")
    private String title;
    @NotEmpty(message = "Content name can not be empty")
    private String content;
    @NotEmpty(message="Place can not be empty")
    private String place;
}
