package com.hcmute.hotel.model.payload.request.Room;

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
public class UpdateRoomRequest {
    @NotEmpty(message = "Room name can not be empty")
    private String name;
    @NotNull(message = "Guess number can not be null")
    private int guestNumber;
    @NotNull(message = "Number of room can not be null")
    private int numberOfRoom;
    @NotNull(message = "Price can not be null")
    private int price;
    private boolean isHidden;
}
