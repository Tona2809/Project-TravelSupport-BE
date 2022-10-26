package com.hcmute.hotel.mapping;

import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.Stay.AddNewStayRequest;
import com.hcmute.hotel.model.payload.request.Stay.UpdateStayRequest;

public class StayMapping {
    public static StayEntity addReqToEntity(AddNewStayRequest addNewStayRequest, UserEntity user)
    {
        StayEntity stayEntity = new StayEntity();
        stayEntity.setName(addNewStayRequest.getName());
        stayEntity.setProvinceId(addNewStayRequest.getProvinceId());
        stayEntity.setAddressDescription(addNewStayRequest.getAddressDescription());
        stayEntity.setStayDescription(addNewStayRequest.getStayDescription());
        stayEntity.setTimeOpen(addNewStayRequest.getTimeOpen());
        stayEntity.setTimeClose(addNewStayRequest.getTimeClose());
        stayEntity.setHost(user);
        stayEntity.setMaxPeople(addNewStayRequest.getMaxPeople());
        stayEntity.setPrice(addNewStayRequest.getPrice());
        stayEntity.setRoomNumber(addNewStayRequest.getRoomNumber());
        stayEntity.setBathNumber(addNewStayRequest.getBathNumber());
        stayEntity.setBedroomNumber(addNewStayRequest.getBedroomNumber());
        stayEntity.setBedNumber(addNewStayRequest.getBedNumber());
        stayEntity.setType(addNewStayRequest.getType());
        return stayEntity;
    }
    public static StayEntity updateReqToEntity(UpdateStayRequest updateStayRequest,StayEntity stayEntity)
    {
        stayEntity.setName(updateStayRequest.getName());
        stayEntity.setProvinceId(updateStayRequest.getProvinceId());
        stayEntity.setAddressDescription(updateStayRequest.getAddressDescription());
        stayEntity.setStayDescription(updateStayRequest.getStayDescription());
        stayEntity.setTimeOpen(updateStayRequest.getTimeOpen());
        stayEntity.setTimeClose(updateStayRequest.getTimeClose());
        stayEntity.setMaxPeople(updateStayRequest.getMaxPeople());
        stayEntity.setPrice(updateStayRequest.getPrice());
        stayEntity.setRoomNumber(updateStayRequest.getRoomNumber());
        stayEntity.setBathNumber(updateStayRequest.getBathNumber());
        stayEntity.setBedroomNumber(updateStayRequest.getBedroomNumber());
        stayEntity.setBedNumber(updateStayRequest.getBedNumber());
        stayEntity.setType(updateStayRequest.getType());
        return stayEntity;
    }
}
