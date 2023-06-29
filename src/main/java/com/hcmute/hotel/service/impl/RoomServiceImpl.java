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
    public List<RoomEntity> findRoomsByStayId(String stayId) {
        List<RoomEntity> list = roomRepository.findAllByStayId(stayId);
        return list;
    }

    @Override
    public List<RoomEntity> findRoomByListId(List<String> listId) {
        List<RoomEntity> list = roomRepository.findByListId(listId);
        return list;
    }

}
