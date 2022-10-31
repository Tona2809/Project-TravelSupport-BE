package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.common.AppUserRole;
import com.hcmute.hotel.common.UserStatus;
import com.hcmute.hotel.model.entity.RoleEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.repository.RoleRepository;
import com.hcmute.hotel.repository.UserRepository;
import com.hcmute.hotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public List<UserEntity> search(String keyword , UserStatus userStatus, AppUserRole userRole, int page, int size) {
        List<UserEntity> result = userRepository.search( keyword, userStatus, userRole,page, size);
        return result;
    }

    @Override
    public UserEntity register(UserEntity user, AppUserRole role) {
        RoleEntity roleEntity = roleRepository.findByName(role);
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(roleEntity);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public UserEntity findByPhone(String phone) {
        Optional<UserEntity> user = userRepository.findByPhone(phone);
        if (user.isEmpty())
            return null;
        return user.get();
    }
    @Override
    public UserEntity findByEmail(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isEmpty())
            return null;
        return user.get();
    }

    @Override
    public UserEntity findById(String uuid) {
        Optional<UserEntity> user = userRepository.findById(uuid);
        if (user.isEmpty())
            return null;
        return user.get();
    }

    @Override
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public UserEntity findByVerificationCode(String code) {
        Optional<UserEntity> user = userRepository.findByVerificationCode(code);
        if(user.isEmpty())
            return null;
        return user.get();
    }
}
