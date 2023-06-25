package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.RoomEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public interface RoomService {
    RoomEntity addRoom(RoomEntity room);

    RoomEntity findRoomById(String id);

    List<RoomEntity> getAllRoomByStayID(String stayId);

    void deleteById(String id);
}
