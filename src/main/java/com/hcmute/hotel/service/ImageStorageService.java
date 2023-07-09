package com.hcmute.hotel.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



@Component
@Service
public interface ImageStorageService {
    String saveHotelImage(MultipartFile file, String fileName);
    String saveProvinceImage(MultipartFile file,String fileName);
    String saveAmenitiesImage(MultipartFile file,String fileName);
    String saveUserImage(MultipartFile file,String fileName);

    String savePlaceImage(MultipartFile file,String fileName);
}
