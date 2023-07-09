package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceRepository extends JpaRepository<PlaceEntity, String> {

    @Query(value = "SELECT * FROM places WHERE (?1 IS NULL OR " +
            "(CONCAT(name, ' ', address_description) LIKE CONCAT('%', COALESCE(?1, ''), '%'))) AND " +
            "((6371 * 2 * ASIN(SQRT(POWER(SIN((?2 - ABS(places.latitude)) * PI() / 180 / 2), 2) + " +
            "COS(?2 * PI() / 180) * COS(ABS(places.latitude) * PI() / 180) * " +
            "POWER(SIN((?3 - places.longitude) * PI() / 180 / 2), 2)))) <= 20 OR (?2=0 AND ?3=0)) AND (?4 IS NULL OR province=?4)", nativeQuery = true)
    List<PlaceEntity> searchPlace(String key, double latitude, double longitude, String provinceId);

    List<PlaceEntity> getAllByProvinceId(String provinceId);
}
