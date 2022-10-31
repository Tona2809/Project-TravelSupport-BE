package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, String> {
Optional<UserEntity> findByPhone(String phone);
@Query(value = "Select * from users where email=?1",nativeQuery = true)
Optional<UserEntity> findByEmail(String email);
@Query(value = "Select * from users where Verification_code=?1",nativeQuery = true)
Optional<UserEntity> findByVerificationCode(String code);
}
