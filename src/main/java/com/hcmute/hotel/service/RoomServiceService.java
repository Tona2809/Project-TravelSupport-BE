package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.entity.RoomEntity;
import com.hcmute.hotel.model.entity.RoomServiceEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@Service
public interface RoomServiceService {

    RoomServiceEntity addRoomService(RoomServiceEntity roomService);

    RoomServiceEntity findRoomServiceById(String id);

    List<RoomServiceEntity> getAllRoomSerivceByRoomID(String stayId);

    void deleteById(String id);

    RoomServiceEntity addImage(MultipartFile file, RoomServiceEntity roomService);
}
