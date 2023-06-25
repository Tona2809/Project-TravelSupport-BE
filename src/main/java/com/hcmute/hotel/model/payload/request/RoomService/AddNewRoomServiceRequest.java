package com.hcmute.hotel.model.payload.request.RoomService;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Setter
@Getter
public class AddNewRoomServiceRequest {

    @NotEmpty(message = "Room service can not be empty")
    private String name;



}
