package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@Service
public interface VoucherService {

    VoucherEntity addVoucher(VoucherEntity entity);
    List<VoucherEntity> getAllVouchers();
    List<VoucherEntity> getAllVouchersByRoom(String roomId);


    VoucherEntity getVoucherById(String id);

    void deleteById(String id);

    boolean findByName(String name);

    VoucherEntity userVoucher(UserEntity user,VoucherEntity voucher);

    boolean findByNameAndId(String name, String id);

    List<VoucherEntity> getAllVoucherByUser(String userId, String roomId);

    List<VoucherEntity> getAllVoucherByStay(String stayId);
}
