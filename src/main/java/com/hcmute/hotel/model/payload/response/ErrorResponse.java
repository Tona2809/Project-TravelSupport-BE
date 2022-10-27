package com.hcmute.hotel.model.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private String error;
    private String message;

    private String messageDescription;

    public ErrorResponse(String error,String message, String messageDescription) {
        this.error=error;
        this.message = message;
        this.messageDescription = messageDescription;
    }

    public ErrorResponse() {
    }
}
