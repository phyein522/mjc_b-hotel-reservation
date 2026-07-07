package com.mjc.hotel.promotion;

import lombok.Getter;

@Getter
public enum DiscountTypeEnum {

    /**
     * 퍼센트 할인
     */
    RATE(1),

    /**
     * 금액 할인
     */
    AMOUNT(2),
    /**
     * 패키지 혜택
     */
    PACKAGE(3);

    private final int value;

    DiscountTypeEnum(int value) {
        this.value = value;
    }
}
