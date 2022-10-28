package com.hcmute.hotel.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hcmute.hotel.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageStorageServiceImpl implements ImageStorageService {
    Map r;
    public Cloudinary cloudinary(){
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dk9djypiq",
                "api_key", "992218256251318",
                "api_secret", "zjfJiyGoU2BNoBxnltC4Jw1fMnw"));
        return cloudinary;
    }

    @Override
    public String saveHotelImage(MultipartFile file, String fileName) {
        try {
            r = this.cloudinary().uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type","auto","upload_preset","bang","public_id","stay_image/"+fileName));
        } catch (IOException e) {
            throw new RuntimeException("Upload fail");
        }
        return (String) r.get("secure_url");
    }
}
