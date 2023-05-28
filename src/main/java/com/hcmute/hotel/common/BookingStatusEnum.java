package com.hcmute.hotel.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum BookingStatusEnum {
    NULL("null"),
    PENDING("1"),
    SUSSCESS("2"),
    REJECT("3");
    private String status;
}
