package com.hcmute.hotel.mapping;

import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.payload.request.Province.AddNewProvinceRequest;
import com.hcmute.hotel.model.payload.request.Province.UpdateProvinceRequest;

import java.time.LocalDateTime;
public class ProvinceMapping {

    public static ProvinceEntity addProvinceToEntity(AddNewProvinceRequest addNewProvinceRequest) {
        ProvinceEntity province = new ProvinceEntity();
        province.setName(addNewProvinceRequest.getName());
        Boolean isHidden = false;
        LocalDateTime time = LocalDateTime.now();
        int number = 0;
        province.setHidden(isHidden);
        province.setCreateAt(time);
        province.setPlaceCount(number);
        return province;
    }

    public static ProvinceEntity updateProvinceToEntity(UpdateProvinceRequest updateProvinceRequest, ProvinceEntity province) {
        province.setName(updateProvinceRequest.getName());
        LocalDateTime time = LocalDateTime.now();
        province.setHidden(updateProvinceRequest.isHidden());
        province.setUpdateAt(time);
        return province;
    }
}
