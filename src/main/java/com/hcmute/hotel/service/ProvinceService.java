package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.ProvinceEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public interface ProvinceService {
    List<ProvinceEntity> getAllProvinces();

    ProvinceEntity getProvinceById(int id);

    ProvinceEntity saveProvince(ProvinceEntity province);

    void deleteById(int id);

    boolean findByName(String name);

    boolean findByNameAndId(String name, int id);
}

