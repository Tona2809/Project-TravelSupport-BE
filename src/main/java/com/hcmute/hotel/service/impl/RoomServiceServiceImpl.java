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
    public RoomServiceEntity addRoomService(RoomServiceEntity roomService) {
        return roomServiceRepository.save(roomService);
    }

    @Override
    public RoomServiceEntity findRoomServiceById(String id) {
        Optional<RoomServiceEntity> room = roomServiceRepository.findById(id);
        return room.isEmpty() ? null : room.get();
    }

    @Override
    public List<RoomServiceEntity> getAllRoomSerivceByRoomID(String roomId) {
        List<RoomServiceEntity> roomServiceEntityList = roomServiceRepository.findAllByRoomId(roomId);
        return roomServiceEntityList;
    }

    @Override
    public void deleteById(String id) {
        roomServiceRepository.deleteById(id);
    }

    @Override
    public RoomServiceEntity addImage(MultipartFile file, RoomServiceEntity roomService) throws FileNotImageException {
        if (!isImageFile(file))
        {
            throw  new FileNotImageException("This file is not Image type");
        }
        else
        {
            String uuid = String.valueOf(UUID.randomUUID());
            String url = imageStorageService.saveProvinceImage(file,roomService.getId()+ "/img" + uuid);
            roomService.setImgLink(url);
            roomService = roomServiceRepository.save(roomService);
            return roomService;
        }
    }
    public boolean isImageFile(MultipartFile file) {
        return Arrays.asList(new String[] {"image/png","image/jpg","image/jpeg", "image/bmp"})
                .contains(file.getContentType().trim().toLowerCase());
    }
}
