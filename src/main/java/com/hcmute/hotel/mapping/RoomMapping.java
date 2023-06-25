package com.hcmute.hotel.mapping;

import com.hcmute.hotel.model.entity.RoomEntity;
import com.hcmute.hotel.model.payload.request.Room.AddNewRoomRequest;
import com.hcmute.hotel.model.payload.request.Room.UpdateRoomRequest;

public class RoomMapping {

    public static RoomEntity addRoomtoEntity(AddNewRoomRequest addNewRoomRequest) {
        RoomEntity room = new RoomEntity();
        room.setRoomName(addNewRoomRequest.getName());
        Boolean isHidden = false;
        room.setHidden(isHidden);
        room.setNumberOfRoom(addNewRoomRequest.getNumberOfRoom());
        room.setPrice(addNewRoomRequest.getPrice());
        room.setGuestNumber(addNewRoomRequest.getGuestNumber());
        return room;
    }

    public static RoomEntity updateRoomtoEntity(UpdateRoomRequest updateRoomRequest, RoomEntity room) {
        room.setRoomName(updateRoomRequest.getName());
        room.setHidden(updateRoomRequest.isHidden());
        room.setPrice(updateRoomRequest.getPrice());
        room.setGuestNumber(updateRoomRequest.getGuestNumber());
        room.setNumberOfRoom(updateRoomRequest.getNumberOfRoom());
        return room;
    }
}
