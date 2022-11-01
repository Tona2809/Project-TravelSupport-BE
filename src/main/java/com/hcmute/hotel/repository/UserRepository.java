package com.hcmute.hotel.repository;

import com.hcmute.hotel.common.AppUserRole;
import com.hcmute.hotel.common.UserStatus;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, String>, UserRepositoryCustom {
Optional<UserEntity> findByPhone(String phone);
@Query(value = "Select * from users where email=?1",nativeQuery = true)
Optional<UserEntity> findByEmail(String email);
@Query(value = "Select * from users where Verification_code=?1",nativeQuery = true)
Optional<UserEntity> findByVerificationCode(String code);
    List<UserEntity> search(String keyword, UserStatus userStatus, AppUserRole userRole, int page, int size);
}
