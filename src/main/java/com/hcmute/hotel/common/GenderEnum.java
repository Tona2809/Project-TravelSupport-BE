package com.hcmute.hotel.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum GenderEnum {
    MALE("Male"),
    FEMALE("Female");
    private String gender;
}
