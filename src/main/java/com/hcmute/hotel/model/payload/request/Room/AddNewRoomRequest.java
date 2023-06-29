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
public class AddNewRoomRequest {
    @NotEmpty
    private String roomName;
    @NotNull
    private int numberOfRoom;
    @NotNull
    private int price;
    @NotNull
    private int numberOfGuest;
    @NotEmpty
    private String stayId;
}
