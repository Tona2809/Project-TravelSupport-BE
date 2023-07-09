package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.handler.FileNotImageException;
import com.hcmute.hotel.model.entity.PlaceEntity;
import com.hcmute.hotel.model.entity.PlaceImageEntity;
import com.hcmute.hotel.model.entity.StayImageEntity;
import com.hcmute.hotel.repository.PlaceImageRepository;
import com.hcmute.hotel.repository.PlaceRepository;
import com.hcmute.hotel.service.ImageStorageService;
import com.hcmute.hotel.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
    private final PlaceRepository placeRepository;

    private final ImageStorageService imageStorageService;

    private final PlaceImageRepository placeImageRepository;

    @Override
    public PlaceEntity addPlace(PlaceEntity place) {
        return placeRepository.save(place);
    }

    @Override
    public PlaceEntity getPlaceById(String id) {
        Optional<PlaceEntity> place = placeRepository.findById(id);
        return place.isEmpty() ? null : place.get();
    }

    @Override
    public List<PlaceImageEntity> addPlaceImg(MultipartFile[] files, PlaceEntity place) {
        List<PlaceImageEntity> imgList= new ArrayList<>();
        if (files== null || files.length==0)
        {
            return null;
        }
        for (MultipartFile file:files)
        {
            if (!isImageFile(file))
                throw new FileNotImageException("This file is not Image type");
            else {
                if (place.getPlaceImage()==null)
                {
                    place.setPlaceImage(new HashSet<>());
                }
                PlaceImageEntity img = new PlaceImageEntity();
                String url = imageStorageService.savePlaceImage(file,place.getName().trim()+ "/img" + img.getImgId());
                place.getPlaceImage().add(img);
                placeRepository.save(place);
                img.setImgLink(url);
                img.setPlace(place);
                placeImageRepository.save(img);
                imgList.add(img);
            }
        }
        return imgList;
    }

    @Override
    public PlaceImageEntity getPlaceImageByLink(String link) {
        Optional<PlaceImageEntity> placeImg = placeImageRepository.getByImgLink(link);
        return placeImg.isEmpty() ? null : placeImg.get();
    }

    @Override
    public void deletePlaceImage(PlaceImageEntity placeImage) {
        placeImageRepository.delete(placeImage);
    }

    @Override
    public List<PlaceEntity> searchPlace(String searchKey, double latitude, double longitude, String provinceId) {
        return placeRepository.searchPlace(searchKey, latitude, longitude, provinceId);
    }

    @Override
    public List<PlaceEntity> getAllByProvince(String provinceId) {
        return placeRepository.getAllByProvinceId(provinceId);
    }

    public boolean isImageFile(MultipartFile file) {
        return Arrays.asList(new String[] {"image/png","image/jpg","image/jpeg", "image/bmp"})
                .contains(file.getContentType().trim().toLowerCase());
    }
}
