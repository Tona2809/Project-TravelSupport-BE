package com.hcmute.hotel.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum StayStatus {
    NULL("null"),
    AVAILABLE("0"),
    UNAVAILABLE("1");
private String name;
}
