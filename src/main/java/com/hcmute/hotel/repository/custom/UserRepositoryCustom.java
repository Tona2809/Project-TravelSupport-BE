package com.hcmute.hotel.repository.custom;

import com.hcmute.hotel.common.AppUserRole;
import com.hcmute.hotel.common.UserStatus;
import com.hcmute.hotel.model.entity.UserEntity;

import java.util.List;

public interface UserRepositoryCustom {
    List<UserEntity> search(String keyword, UserStatus userStatus, AppUserRole userRole, int page, int size);
}
