package com.hcmute.hotel.model.payload.request.RoomService;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
@Data
@NoArgsConstructor
@Setter
@Getter
public class UpdateRoomServiceRequest {

    @NotEmpty(message = "Room service can not be empty")
    private String name;
}
