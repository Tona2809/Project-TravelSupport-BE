package com.hcmute.hotel.repository;

import com.hcmute.hotel.common.AppUserRole;
import com.hcmute.hotel.model.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<RoleEntity,String> {
    RoleEntity findByName(AppUserRole name);
    Boolean existsByName(String roleName);
}
