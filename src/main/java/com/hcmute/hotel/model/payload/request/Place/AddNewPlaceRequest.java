package com.hcmute.hotel.model.payload.request.Place;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@Setter
@Getter
public class AddNewPlaceRequest {

    final String EMPTY_MESSAGE = "cannot be empty";
    @NotEmpty(message = "name"+ EMPTY_MESSAGE)
    String name;



}
