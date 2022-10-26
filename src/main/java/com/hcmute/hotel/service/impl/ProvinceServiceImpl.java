package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.repository.ProvinceRepository;
import com.hcmute.hotel.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProvinceServiceImpl implements ProvinceService {

    final ProvinceRepository provinceRepository;

    @Override
    public List<ProvinceEntity> getAllProvinces() {
        List<ProvinceEntity> provinceEntityList = provinceRepository.findAll();
        return provinceEntityList;
    }

    @Override
    public ProvinceEntity getProvinceById(int id) {
        Optional<ProvinceEntity> provinceEntity = provinceRepository.findById(id);
        if (provinceEntity.isEmpty())
            return null;
        return provinceEntity.get();
    }

    @Override
    public ProvinceEntity saveProvince(ProvinceEntity province) {
        return provinceRepository.save(province);
    }

    @Override
    public void deleteById(int id) {
        provinceRepository.deleteById(id);
    }

    @Override
    public boolean findByName(String name) {
        List<ProvinceEntity> provinceEntityList = provinceRepository.findByName(name);
        if (provinceEntityList.size() == 0)
            return false;
        else return true;
    }
}
