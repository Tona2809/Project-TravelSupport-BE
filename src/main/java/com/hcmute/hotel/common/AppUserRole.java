package com.hcmute.hotel.common;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum AppUserRole {
    ROLE_OWNER("ROLE_OWNER"), ROLE_USER("ROLE_USER"), ROLE_ADMIN("ROLE_ADMIN");
    private final String name;
}