package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public interface StayService {
    StayEntity saveStay(StayEntity stay);
    List<StayEntity> getAllStay();
    StayEntity getStayById(String id);
    void deleteStay(String id);

    List<StayEntity> getStayByUser(UserEntity user);
}
