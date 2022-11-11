package com.hcmute.hotel.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum StaySortEnum {
    MAX_PEOPLE("max_people"),
    PRICE("price");
    private String name;
}
