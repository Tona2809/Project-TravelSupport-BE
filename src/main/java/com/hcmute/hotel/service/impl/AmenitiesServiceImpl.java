package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.handler.FileNotImageException;
import com.hcmute.hotel.model.entity.AmenitiesEntity;
import com.hcmute.hotel.repository.AmenitiesRepository;
import com.hcmute.hotel.service.AmenitiesService;
import com.hcmute.hotel.service.ImageStorageService;
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
public class AmenitiesServiceImpl implements AmenitiesService {
    private final AmenitiesRepository amenitiesRepository;
    private final ImageStorageService imageStorageService;
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

    @Override
    public String addAmenitiesIcon(MultipartFile file,AmenitiesEntity amenities) throws FileNotImageException {
        if (!isImageFile(file))
        {
            throw  new FileNotImageException("This file is not Image type");
        }
        else
        {
            String uuid = String.valueOf(UUID.randomUUID());
            String url = imageStorageService.saveAmenitiesImage(file, amenities.getId()+ "/img" + uuid);
            return url;
        }
    }
    public boolean isImageFile(MultipartFile file) {
        return Arrays.asList(new String[] {"image/png","image/jpg","image/jpeg", "image/bmp"})
                .contains(file.getContentType().trim().toLowerCase());
    }
}
