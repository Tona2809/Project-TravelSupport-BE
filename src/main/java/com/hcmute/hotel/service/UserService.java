package com.hcmute.hotel.service;

import com.hcmute.hotel.common.AppUserRole;
import com.hcmute.hotel.common.UserStatus;
import com.hcmute.hotel.model.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Component
@Service
public interface UserService {
UserEntity register(UserEntity user,String role);

UserEntity findByPhone(String phone);

    UserEntity findById(String uuid);
    public UserEntity findByEmail(String email);
    UserEntity save(UserEntity user);

    UserEntity findByVerificationCode(String code);

    List<UserEntity> search(String keyword, UserStatus userstatus, AppUserRole userRole, int page, int size);
}
