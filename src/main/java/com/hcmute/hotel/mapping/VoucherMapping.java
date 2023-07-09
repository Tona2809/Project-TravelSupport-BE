package com.hcmute.hotel.mapping;

import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.entity.VoucherEntity;
import com.hcmute.hotel.model.payload.request.Province.UpdateProvinceRequest;
import com.hcmute.hotel.model.payload.request.Stay.AddNewStayRequest;
import com.hcmute.hotel.model.payload.request.Voucher.AddVoucherRequest;
import com.hcmute.hotel.model.payload.request.Voucher.UpdateVoucherRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class VoucherMapping {
    public static VoucherEntity addReqToEntity(AddVoucherRequest addNewVocuherRequest)
    {
        VoucherEntity voucherEntity = new VoucherEntity();
        voucherEntity.setName(addNewVocuherRequest.getName());
        voucherEntity.setDiscount(addNewVocuherRequest.getDiscount());
        voucherEntity.setCreateAt(LocalDateTime.now(ZoneId.of("GMT+07:00")));
        voucherEntity.setUpdateAt(LocalDateTime.now(ZoneId.of("GMT+07:00")));
        voucherEntity.setHidden(true);
        voucherEntity.setExpirationDate(addNewVocuherRequest.getExpiredDate());
        voucherEntity.setRemainingQuantity(0);
        voucherEntity.setQuantity(addNewVocuherRequest.getQuantity());
        return voucherEntity;
    }
    public static VoucherEntity updateVoucherToEntity(UpdateVoucherRequest updateVoucherRequest,VoucherEntity voucher) {
        voucher.setHidden(voucher.isHidden());
        voucher.setUpdateAt(LocalDateTime.now(ZoneId.of("GMT+07:00")));
        voucher.setExpirationDate(updateVoucherRequest.getExpiredDate());
        return voucher;
    }
}
