package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.entity.VoucherEntity;
import com.hcmute.hotel.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface VoucherRepository extends JpaRepository<VoucherEntity, String> {
    List<VoucherEntity> findByName(String name);
    @Query(value = "select * from voucher where name like %?%  and id <> ?", nativeQuery = true)
    List<VoucherEntity> findByNameAndId(String name, String id);
}
