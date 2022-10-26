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
    @NotNull(message = "province id"+EMPTY_MESSAGE)
    int provinceId;
    @NotEmpty(message = "address description" + EMPTY_MESSAGE)
    String addressDescription;
    @NotEmpty(message = "stay description"+ EMPTY_MESSAGE)
    String stayDescription;
    LocalDateTime timeOpen;
    @Future(message = "time close is not valid")
    LocalDateTime timeClose;
    @NotNull(message = "max people"+EMPTY_MESSAGE)
    @Min(value = 1,message = "max people must greater than 1")
    int maxPeople;
    @NotNull(message = "price"+EMPTY_MESSAGE)
    @Min(value = 1,message = "price must greater than 1")
    int price;
    @NotNull(message = "room number"+EMPTY_MESSAGE)
    @Min(value = 1,message = "number of room must greater than 1")
    int roomNumber;
    @NotNull(message = "bath number"+EMPTY_MESSAGE)
    @Min(value = 1,message = "number of bath must greater than 1")
    int bathNumber;
    @NotNull(message = "bedroom number"+EMPTY_MESSAGE)
    @Min(value = 1,message = "number of bedroom must greater than 1")
    int bedroomNumber;
    @NotNull(message = "bed number"+EMPTY_MESSAGE)
    @Min(value = 1,message = "number of bed must greater than 1")
    int bedNumber;
    @NotEmpty(message = "type"+EMPTY_MESSAGE)
    String type;

}
