package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String>, UserRepositoryCustom {
    Optional<UserEntity> findByPhone(String phone);

//    @Query(value = "select * from users where users.full_name LIKE %?1%" +
//            " OR email like %?1%" +
//            " OR phone like %?1%", nativeQuery = true)
//    public List<UserEntity> search(String keyword);


}
