package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.ProvinceEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@Service
public interface ProvinceService {
    List<ProvinceEntity> getAllProvinces();

    ProvinceEntity getProvinceById(String id);

    ProvinceEntity saveProvince(ProvinceEntity province);

    void deleteById(String id);

    boolean findByName(String name);

    boolean findByNameAndId(String name, String id);
    ProvinceEntity addImage(MultipartFile file,ProvinceEntity province);
}

