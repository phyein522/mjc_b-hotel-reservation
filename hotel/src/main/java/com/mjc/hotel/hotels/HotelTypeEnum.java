package com.mjc.hotel.hotels;

import lombok.Getter;

@Getter
public enum HotelTypeEnum {

    /**
     * 호텔
     */
    HOTEL(1),

    /**
     * 리조트
     */
    RESORT(2),

    /**
     * 펜션/게스트하우스
     */
    PENSION_GUESTHOUSE(3);

    private final int value;

    HotelTypeEnum(int value) {
        this.value = value;
    }
}