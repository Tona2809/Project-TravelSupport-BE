package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
@EnableJpaRepositories

public interface StayRepository extends JpaRepository<StayEntity,String> {
    List<StayEntity> findAllByHost(UserEntity user);
    @Query(value = "Select * from stays where province=?1",nativeQuery = true)
    Page<StayEntity> findALlStayByProvinceId(String provinceId,Pageable pageable);
    List<StayEntity> findAllByProvince(ProvinceEntity province);
}
