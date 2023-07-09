package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.handler.FileNotImageException;
import com.hcmute.hotel.model.entity.RoomEntity;
import com.hcmute.hotel.model.entity.RoomServiceEntity;
import com.hcmute.hotel.repository.RoomServiceRepository;
import com.hcmute.hotel.service.ImageStorageService;
import com.hcmute.hotel.service.RoomServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomServiceServiceImpl implements RoomServiceService {

    final ImageStorageService imageStorageService;
    private final RoomServiceRepository roomServiceRepository;
    @Override
    public List<RoomServiceEntity> addRoomService(List<RoomServiceEntity> roomService) {
        return roomServiceRepository.saveAll(roomService);
    }

    @Override
    public RoomServiceEntity saveRoomService(RoomServiceEntity roomService) {
        return roomServiceRepository.save(roomService);
    }

    @Override
    public RoomServiceEntity findRoomServiceById(String id) {
        Optional<RoomServiceEntity> room = roomServiceRepository.findById(id);
        return room.isEmpty() ? null : room.get();
    }

    @Override
    public RoomServiceEntity findRoomServiceByName(String name) {
        Optional<RoomServiceEntity> roomServiceEntity = roomServiceRepository.findByRoomServiceName(name);
        return roomServiceEntity.isEmpty() ? null : roomServiceEntity.get();
    }

    @Override
    public List<RoomServiceEntity> getAllRoomSerivceByRoomID(String roomId) {
        List<RoomServiceEntity> roomServiceEntityList = roomServiceRepository.findAllByRoomId(roomId);
        return roomServiceEntityList;
    }

    @Override
    public List<RoomServiceEntity> getAllRoomService() {
        return roomServiceRepository.findAll();
    }

    @Override
    public void deleteById(String id) {
        roomServiceRepository.deleteById(id);
    }


}
