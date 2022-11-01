package com.hcmute.hotel.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum StayStatus {
AVAILABLE("Available"),UNAVAILABLE("Unavailable");
private String name;
}
