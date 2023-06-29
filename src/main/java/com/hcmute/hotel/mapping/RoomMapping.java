package com.hcmute.hotel.mapping;

import com.hcmute.hotel.model.entity.RoomEntity;
import com.hcmute.hotel.model.payload.request.Room.AddNewRoomRequest;
import com.hcmute.hotel.model.payload.request.Room.UpdateRoomRequest;

public class RoomMapping {
    public static RoomEntity addToEntity(AddNewRoomRequest addNewRoomRequest)
    {
        RoomEntity room = new RoomEntity();
        room.setRoomName(addNewRoomRequest.getRoomName());
        room.setNumberOfRoom(addNewRoomRequest.getNumberOfRoom());
        room.setGuestNumber(addNewRoomRequest.getNumberOfGuest());
        room.setPrice(addNewRoomRequest.getPrice());
        return room;
    }

    public static RoomEntity updateToEntity(RoomEntity room, UpdateRoomRequest updateRoomRequest)
    {
        room.setRoomName(updateRoomRequest.getRoomName());
        room.setNumberOfRoom(updateRoomRequest.getNumberOfRoom());
        room.setGuestNumber(updateRoomRequest.getNumberOfGuest());
        room.setPrice(updateRoomRequest.getPrice());
        return room;
    }
}
