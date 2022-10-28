package com.hcmute.hotel.model.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponseMap {
    public ErrorResponseMap(String message) {
        super();
        this.message = message;

    }
    private Boolean success;
    private int status;
    private String message;
    private Map<String,String> details;
}
