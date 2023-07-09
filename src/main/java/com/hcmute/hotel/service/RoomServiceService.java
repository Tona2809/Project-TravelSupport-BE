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

    List<RoomServiceEntity> addRoomService(List<RoomServiceEntity> roomService);

    RoomServiceEntity saveRoomService(RoomServiceEntity roomService);

    RoomServiceEntity findRoomServiceById(String id);

    RoomServiceEntity findRoomServiceByName(String name);

    List<RoomServiceEntity> getAllRoomSerivceByRoomID(String stayId);

    List<RoomServiceEntity> getAllRoomService();

    void deleteById(String id);

}
