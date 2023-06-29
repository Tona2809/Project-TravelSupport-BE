package com.hcmute.hotel.model.payload.request.Stay;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Setter
@Getter
public class AddNewStayRequest {
    final String EMPTY_MESSAGE ="cannot be empty";
    @NotEmpty(message = "name"+EMPTY_MESSAGE)
    String name;
    @NotEmpty(message = "province id"+EMPTY_MESSAGE)
    String provinceId;
    @NotEmpty(message = "address description" + EMPTY_MESSAGE)
    String addressDescription;
    @NotEmpty(message = "stay description"+ EMPTY_MESSAGE)
    String stayDescription;
    LocalDateTime timeOpen;
    @Future(message = "time close is not valid")
    LocalDateTime timeClose;
    @NotEmpty(message = "type"+EMPTY_MESSAGE)
    String type;

}
