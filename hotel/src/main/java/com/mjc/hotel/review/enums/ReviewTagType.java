package com.mjc.hotel.review.enums;

import java.util.Arrays;

public enum ReviewTagType {
    CLEAN(0),
    KIND(1),
    GOOD_LOCATION(2),
    QUIET(3),
    GOOD_VALUE(4),
    GOOD_VIEW(5),
    DELICIOUS_BREAKFAST(6),
    EASY_PARKING(7),
    UNCLEAN(8),
    NOISY(9),
    BAD_LOCATION(10),
    EXPENSIVE(11),
    UNKIND(12),
    INCONVENIENT(13);

    private final Integer code;

    ReviewTagType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static ReviewTagType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(value -> value.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ReviewTagType code: " + code));
    }
}
