package com.hcmute.hotel.model.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SuccessResponse<O> {
    private Boolean success;
    private int status;
    private String message;
    private Map<String,Object> data;
    public SuccessResponse(){
        this.data = new HashMap<>();
    }

    public SuccessResponse(Boolean success, int status, String message, Map<String, Object> data) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
    }


}
