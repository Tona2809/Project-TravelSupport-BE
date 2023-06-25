package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.RoomEntity;
import com.hcmute.hotel.repository.RoomRepository;
import com.hcmute.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;

    @Override
    public RoomEntity addRoom(RoomEntity room) {
        return roomRepository.save(room);
    }

    @Override
    public RoomEntity findRoomById(String id) {
        Optional<RoomEntity> room = roomRepository.findById(id);
        return room.isEmpty() ? null : room.get();
    }

    @Override
    public List<RoomEntity> getAllRoomByStayID(String stayId) {
      List<RoomEntity> roomEntityList = roomRepository.findAllByStayId(stayId);
      return roomEntityList;
    }

    @Override
    public void deleteById(String id) {
        roomRepository.deleteById(id);
    }

}
