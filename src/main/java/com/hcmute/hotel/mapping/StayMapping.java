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
        stayEntity.setAddressDescription(addNewStayRequest.getAddressDescription());
        stayEntity.setStayDescription(addNewStayRequest.getStayDescription());
        stayEntity.setTimeOpen(addNewStayRequest.getTimeOpen());
        stayEntity.setTimeClose(addNewStayRequest.getTimeClose());
        stayEntity.setHost(user);
        stayEntity.setType(addNewStayRequest.getType());
        return stayEntity;
    }
    public static StayEntity updateReqToEntity(UpdateStayRequest updateStayRequest,StayEntity stayEntity)
    {
        stayEntity.setName(updateStayRequest.getName());
        stayEntity.setAddressDescription(updateStayRequest.getAddressDescription());
        stayEntity.setStayDescription(updateStayRequest.getStayDescription());
        stayEntity.setTimeOpen(updateStayRequest.getTimeOpen());
        stayEntity.setTimeClose(updateStayRequest.getTimeClose());
        stayEntity.setType(updateStayRequest.getType());
        return stayEntity;
    }
}
