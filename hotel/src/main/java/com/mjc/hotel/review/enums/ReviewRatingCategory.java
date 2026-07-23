package com.mjc.hotel.review.enums;

import java.util.Arrays;

public enum ReviewRatingCategory {
    CLEANLINESS(0),
    SERVICE(1),
    LOCATION(2),
    FACILITY(3),
    VALUE(4),
    COMFORT(5);

    private final Integer code;

    ReviewRatingCategory(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static ReviewRatingCategory fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(value -> value.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ReviewRatingCategory code: " + code));
    }
}
