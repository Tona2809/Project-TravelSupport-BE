package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDateTime;
import java.util.List;
@EnableJpaRepositories

public interface StayRepository extends JpaRepository<StayEntity,String> {
    List<StayEntity> findAllByHost(UserEntity user);
    @Query(value = "Select * from stays where province=?1",nativeQuery = true)
    Page<StayEntity> findALlStayByProvinceId(String provinceId,Pageable pageable);
    List<StayEntity> findAllByProvince(ProvinceEntity province);
    @Query(value = "Select * from stays  where ((province=?1 or ?1 is null) and (price between ?2 and ?3) and (((?4 between time_open and time_close) or ?4 is null) and ((?5 between time_open and time_close) or ?5 is null)) and (max_people>=?6))",nativeQuery = true)
    Page<StayEntity> searchByCriteria(String provinceId, int minPrice, int maxPrice, LocalDateTime checkinDate, LocalDateTime checkoutDate, int maxPeople, String sort, String orderBy, Pageable paging);
}
