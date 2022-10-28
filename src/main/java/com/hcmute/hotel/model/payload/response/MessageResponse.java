package com.hcmute.hotel.model.payload.response;

import com.hcmute.hotel.constants.ApplicationConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageResponse {
    private String error;
    private String message;
    private String message_description;




    public MessageResponse(String error, String message, String message_description) {
        this.error = error;
        this.message = message;
        this.message_description = message_description;
    }


    // public static final DataResponse SUCCESSFUL = new DataResponse(ApplicationConstants.SUCCESSFUL,
    // "SUCCESSFUL");
    // public static final DataResponse SESSION_EXPIRED = new
    // DataResponse(ApplicationConstants.SESSION_EXPIRED, "SESSION_EXPIRED");
    // public static final DataResponse NOT_FOUND = new DataResponse(ApplicationConstants.NOT_FOUND,
    // "NOT_FOUND");
    // public static final DataResponse FORBIDDEN = new DataResponse(ApplicationConstants.FORBIDDEN,
    // "FORBIDDEN");
    // public static final DataResponse PERMISSION_DENY = new
    // DataResponse(ApplicationConstants.PERMISSION_DENY, "PERMISSION_DENY");
    // public static final DataResponse FAILED = new DataResponse(ApplicationConstants.FAILED,
    // "FAILED");
    // public static final DataResponse AUTH_FAILED = new DataResponse(ApplicationConstants.AUTH_FAILED,
    // "AUTH_FAILED");
    // public static final DataResponse IS_BLOCKED = new DataResponse(ApplicationConstants.IS_BLOCKED,
    // "IS_BLOCKED");
}
