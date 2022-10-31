package com.hcmute.hotel.service;

import com.hcmute.hotel.common.AppUserRole;
import com.hcmute.hotel.common.UserStatus;
import com.hcmute.hotel.model.entity.UserEntity;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public interface UserService {


    List<UserEntity> search(String keyword, UserStatus userStatus, AppUserRole userRole, int page, int size);

    UserEntity register(UserEntity user, AppUserRole role);

    UserEntity findByPhone(String phone);

    UserEntity findById(String uuid);

    UserEntity save(UserEntity user);
}
