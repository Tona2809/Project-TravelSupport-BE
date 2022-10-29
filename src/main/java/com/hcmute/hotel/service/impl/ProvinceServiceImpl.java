package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.handler.FileNotImageException;
import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.repository.ProvinceRepository;
import com.hcmute.hotel.service.ImageStorageService;
import com.hcmute.hotel.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProvinceServiceImpl implements ProvinceService {

    final ProvinceRepository provinceRepository;
    final ImageStorageService imageStorageService;

    @Override
    public List<ProvinceEntity> getAllProvinces() {
        List<ProvinceEntity> provinceEntityList = provinceRepository.findAll();
        return provinceEntityList;
    }

    @Override
    public ProvinceEntity getProvinceById(String id) {
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
    public void deleteById(String id) {
        provinceRepository.deleteById(id);
    }

    @Override
    public boolean findByName(String name) {
        List<ProvinceEntity> provinceEntityList = provinceRepository.findByName(name);
        if (provinceEntityList.size() == 0)
            return false;
        else return true;
    }
    @Override
    public boolean findByNameAndId(String name, String id) {
        List<ProvinceEntity> provinceEntityList = provinceRepository.findByNameAndId(name, id);
        if (provinceEntityList.size() == 0)
            return false;
        else return true;
    }

    @Override
    public ProvinceEntity addImage(MultipartFile file, ProvinceEntity province) throws FileNotImageException {
        if (!isImageFile(file))
        {
            throw  new FileNotImageException("This file is not Image type");
        }
        else
        {
            String uuid = String.valueOf(UUID.randomUUID());
            String url = imageStorageService.saveProvinceImage(file,province.getId()+ "/img" + uuid);
            province.setImgLink(url);
            provinceRepository.save(province);
            return province;
        }
    }
    public boolean isImageFile(MultipartFile file) {
        return Arrays.asList(new String[] {"image/png","image/jpg","image/jpeg", "image/bmp"})
                .contains(file.getContentType().trim().toLowerCase());
    }
}
