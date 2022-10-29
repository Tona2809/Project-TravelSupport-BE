package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.handler.FileNotImageException;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.StayImageEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.repository.StayImageRepository;
import com.hcmute.hotel.repository.StayRepository;
import com.hcmute.hotel.service.ImageStorageService;
import com.hcmute.hotel.service.StayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StayServiceImpl implements StayService {
    private final StayRepository stayRepository;
    private final StayImageRepository stayImageRepository;
    private final ImageStorageService imageStorageService;
    @Override
    public StayEntity saveStay(StayEntity stay) {
        return stayRepository.save(stay);
    }

    @Override
    public List<StayEntity> getAllStay() {
        return stayRepository.findAll();
    }

    @Override
    public StayEntity getStayById(String id) {
        Optional<StayEntity> stayEntity = stayRepository.findById(id);
        if (stayEntity.isEmpty())
            return null;
        return stayEntity.get();
    }

    @Override
    public void deleteStay(String id) {
        stayRepository.deleteById(id);
    }

    @Override
    public List<StayEntity> getStayByUser(UserEntity user) {
        return stayRepository.findAllByHost(user);
    }

    @Override
    public List<StayImageEntity> addStayImg(MultipartFile[] files, StayEntity stay) throws FileNotImageException {
        List<StayImageEntity> imgList= new ArrayList<>();
        if (files.length==0)
        {
            return null;
        }
        for (MultipartFile file:files)
        {
            if (!isImageFile(file))
                throw new FileNotImageException("This file is not Image type");
            else {
                StayImageEntity img = new StayImageEntity();
                String url = imageStorageService.saveHotelImage(file,stay.getId()+ "/img" + img.getImgId());
                stay.getStayImage().add(img);
                stayRepository.save(stay);
                img.setImgLink(url);
                img.setStay(stay);
                stayImageRepository.save(img);
                imgList.add(img);
            }
        }
        return imgList;
    }

    @Override
    public StayImageEntity findImgById(String id) {
        Optional<StayImageEntity> stayImage = stayImageRepository.findById(id);
        if (stayImage==null)
            return null;
        return stayImage.get();
    }

    @Override
    public void DeleteImg(String id) {
        stayImageRepository.deleteById(id);
    }

    public boolean isImageFile(MultipartFile file) {
        return Arrays.asList(new String[] {"image/png","image/jpg","image/jpeg", "image/bmp"})
                .contains(file.getContentType().trim().toLowerCase());
    }
}
