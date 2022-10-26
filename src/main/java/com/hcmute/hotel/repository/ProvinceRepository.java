package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface ProvinceRepository extends JpaRepository<ProvinceEntity, Integer> {
    List<ProvinceEntity> findByName(String name);
    @Query(value = "select * from province where name like %?%  and id <> ?", nativeQuery = true)
    List<ProvinceEntity> findByNameAndId(String name, int id);
}
