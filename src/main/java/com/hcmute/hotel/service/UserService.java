package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Component
@Service
public interface UserService {
UserEntity register(UserEntity user,String role);

UserEntity findByPhone(String phone);

    UserEntity findById(String uuid);
    UserEntity save(UserEntity user);
}
