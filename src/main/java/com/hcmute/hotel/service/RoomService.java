package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.RoomEntity;
import net.bytebuddy.asm.Advice;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@Service
public interface RoomService {
    RoomEntity addRoom(RoomEntity room);

    RoomEntity findRoomById(String id);

    List<RoomEntity> findRoomsByStayId(String stayId);

    List<RoomEntity> findRoomByListId(List<String> listId);

    Map<String,Integer> getAvailableRoom(LocalDateTime checkinDate, LocalDateTime checkoutDate, int guestNumber, String stayId, int flexibleNumbers);

    Map<String,Integer> getCurrentAvailableRoom(LocalDateTime checkinDate, LocalDateTime checkoutDate, String stayId);


}
