package com.hcmute.hotel.model.payload.request.StayRating;

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
public class UpdateStayRatingRequest {
    private static final int MIN_RATE = 0;
    private static final int MAX_RATE =5;
    @NotEmpty(message = "id không được để trống")
    private String id;
    @Range(min=MIN_RATE,max=MAX_RATE)
    private int rate;
    @NotEmpty(message = "Nội dung không được để trống")
    private String message;
}
