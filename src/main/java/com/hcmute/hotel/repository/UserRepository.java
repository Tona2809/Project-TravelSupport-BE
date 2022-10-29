package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, String> {
Optional<UserEntity> findByPhone(String phone);
Optional<UserEntity> findByEmail(String email);
}
