package com.mjc.hotel.review.enums;

import java.util.Arrays;

public enum ReviewTripType {
    FAMILY(0),
    COUPLE(1),
    FRIENDS(2),
    BUSINESS(3),
    SOLO(4),
    OTHER(5);

    private final Integer code;

    ReviewTripType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static ReviewTripType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(value -> value.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ReviewTripType code: " + code));
    }
}
