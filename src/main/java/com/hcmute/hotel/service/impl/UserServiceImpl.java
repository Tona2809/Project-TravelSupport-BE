package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.RoleEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.repository.RoleRepository;
import com.hcmute.hotel.repository.UserRepository;
import com.hcmute.hotel.service.UserService;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.description.NamedElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserEntity register(UserEntity user, String role) {
        Optional<RoleEntity> roleEntity=roleRepository.findByName(role);
        if (roleEntity.isEmpty())
            return null;
        else {
            if(user.getRoles()==null){
            Set<RoleEntity> RoleSet=new HashSet<>();
            RoleSet.add(roleEntity.get());
            user.setRoles(RoleSet);
        }
            else
                user.getRoles().add(roleEntity.get());

        }
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
        if(user.isEmpty())
            return null;
        return user.get();
    }

    @Override
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }
}
