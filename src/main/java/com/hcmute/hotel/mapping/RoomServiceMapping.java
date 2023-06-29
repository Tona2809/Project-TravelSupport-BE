package com.hcmute.hotel.mapping;

import com.hcmute.hotel.model.entity.RoomEntity;
import com.hcmute.hotel.model.entity.RoomServiceEntity;
import com.hcmute.hotel.model.payload.request.Room.AddNewRoomRequest;
import com.hcmute.hotel.model.payload.request.Room.UpdateRoomRequest;
import com.hcmute.hotel.model.payload.request.RoomService.AddNewRoomServiceRequest;
import com.hcmute.hotel.model.payload.request.RoomService.UpdateRoomServiceRequest;

public class RoomServiceMapping {
    public static RoomServiceEntity addRoomServicetoEntity(AddNewRoomServiceRequest addNewRoomServiceRequest) {
        RoomServiceEntity roomService = new RoomServiceEntity();
        roomService.setRoomServiceName(addNewRoomServiceRequest.getName());
        return roomService;
    }

    public static RoomServiceEntity updateRoomServicetoEntity(UpdateRoomServiceRequest UpdateRoomServiceRequest, RoomServiceEntity roomService) {
        roomService.setRoomServiceName(UpdateRoomServiceRequest.getName());
        return roomService;
    }
}
