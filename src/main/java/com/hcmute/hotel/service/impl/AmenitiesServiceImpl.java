package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.AmenitiesEntity;
import com.hcmute.hotel.repository.AmenitiesRepository;
import com.hcmute.hotel.service.AmenitiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AmenitiesServiceImpl implements AmenitiesService {
    private final AmenitiesRepository amenitiesRepository;
    @Override
    public AmenitiesEntity addAmenities(AmenitiesEntity entity) {
        return amenitiesRepository.save(entity);
    }

    @Override
    public AmenitiesEntity getAmenitiesByName(String name) {
        Optional<AmenitiesEntity> amenitiesEntity = amenitiesRepository.findByName(name);
        if (amenitiesEntity.isEmpty())
            return null;
        return amenitiesEntity.get();
    }

    @Override
    public List<AmenitiesEntity> getAllAmenities() {
        return amenitiesRepository.findAll();
    }

    @Override
    public AmenitiesEntity getAmenitiesById(String id) {
        Optional<AmenitiesEntity> amenitiesEntity = amenitiesRepository.findById(id);
        if (amenitiesEntity.isEmpty())
            return null;
        return amenitiesEntity.get();
    }

    @Override
    public void deleteAmenities(String id) {
        amenitiesRepository.deleteById(id);
    }
}
