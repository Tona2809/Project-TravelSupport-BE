package com.hcmute.hotel.model.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
public class DataResponse {
    public DataResponse(String message, Map<String, String> details, int status) {
        this.message = message;
        this.details = details;
        this.status = status;
    }
    private int status;
    private String message;
    private Map<String, String> details;
}
