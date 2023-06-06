package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.AmenitiesEntity;
import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.entity.VoucherEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@Service
public interface VoucherService {

    VoucherEntity addAmenities(VoucherEntity entity);
    List<VoucherEntity> getAllVouchers();

    VoucherEntity getVoucherById(String id);

    void deleteById(String id);

    boolean findByName(String name);

    boolean findByNameAndId(String name, String id);
}
