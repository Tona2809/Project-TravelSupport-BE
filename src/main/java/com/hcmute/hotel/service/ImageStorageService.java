package com.hcmute.hotel.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



@Component
@Service
public interface ImageStorageService {
    String saveHotelImage(MultipartFile file, String fileName);
}
