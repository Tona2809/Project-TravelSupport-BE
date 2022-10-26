package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.AmenitiesEntity;
import com.hcmute.hotel.repository.AmenitiesRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
public interface AmenitiesService {
    AmenitiesEntity addAmenities(AmenitiesEntity entity);
    AmenitiesEntity getAmenitiesByName(String name);
    List<AmenitiesEntity> getAllAmenities();
    AmenitiesEntity getAmenitiesById(String id);
    void deleteAmenities(String id);
}
