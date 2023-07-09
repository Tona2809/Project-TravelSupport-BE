package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.PlaceEntity;
import com.hcmute.hotel.model.entity.PlaceImageEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.StayImageEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@Service
public interface PlaceService {
    PlaceEntity addPlace(PlaceEntity place);

    PlaceEntity getPlaceById(String id);

    List<PlaceImageEntity> addPlaceImg(MultipartFile[] files, PlaceEntity place);

    PlaceImageEntity getPlaceImageByLink(String link);

    void deletePlaceImage(PlaceImageEntity placeImage);

    List<PlaceEntity> searchPlace(String searchKey, double latitude, double longitude, String provinceId);

    List<PlaceEntity> getAllByProvince(String provinceId);

}
