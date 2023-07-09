package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.response.StaySearchResponse;
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
    @Query(value = "SELECT stays.id, min(price), max(guest_number) FROM stays inner join rooms on stays.id = rooms.stay_id WHERE (?1 IS NULL OR province = ?1 ) AND (price between ?2 and ?3 ) AND (((?4 BETWEEN time_open AND time_close) OR ?4 IS NULL) AND ((?5 BETWEEN time_open AND time_close) OR ?5 IS NULL)) AND ( guest_number>=?6) AND (status = ?7 OR ?7 = 'NULL') AND (hidden = ?8 OR ?8 IS NULL) AND (CONCAT(name, ' ', address_description) LIKE CONCAT('%', COALESCE(?9, ''), '%')) AND (COALESCE((?10), NULL) IS NULL OR stays.id IN (SELECT stay_id FROM stay_amenities WHERE amenities_id IN (?11) GROUP BY stay_id HAVING COUNT(DISTINCT amenities_id) = ?12)) group by stays.id order by concat('rooms.', ?13, ' ', ?14)",
            countQuery = "SELECT COUNT(*) FROM stays inner join rooms on stays.id = rooms.stay_id WHERE (?1 IS NULL OR province = ?1 ) AND (price between ?2 and ?3 ) AND (((?4 BETWEEN time_open AND time_close) OR ?4 IS NULL) AND ((?5 BETWEEN time_open AND time_close) OR ?5 IS NULL)) AND ( guest_number>=?6) AND (status = ?7 OR ?7 = 'NULL') AND (hidden = ?8 OR ?8 IS NULL) AND (CONCAT(name, ' ', address_description) LIKE CONCAT('%', COALESCE(?9, ''), '%')) AND (COALESCE((?10), NULL) IS NULL OR stays.id IN (SELECT stay_id FROM stay_amenities WHERE amenities_id IN (?11) GROUP BY stay_id HAVING COUNT(DISTINCT amenities_id) = ?12)) group by stays.id order by concat('rooms.', ?13, ' ', ?14)",
            nativeQuery = true)
    Page<Object[]> searchByCriteria(String provinceId, int minPrice, int maxPrice, LocalDateTime checkinDate, LocalDateTime checkoutDate, int maxPeople, String status, boolean hidden, String searchKey, String isEmpty, List<String> amenitiesId, int amenitiesCount, String sort, String orderBy, Pageable paging);
}
