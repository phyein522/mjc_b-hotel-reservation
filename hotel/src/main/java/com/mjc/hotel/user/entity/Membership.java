package com.mjc.hotel.user.entity;

import lombok.Getter;

@Getter
public enum Membership {
    NEW_MEMBER(0)   //신규 가입
    , STANDARD(1)   //일반, 1회 숙박 시
    , GOLD(2)   //3회 숙박 시, 10% 할인, 최대 50000만원 할인
    , VIP(3)    //7회 숙박 시, 13% 할인, 최대 80000만원 할인
    , VVIP(4)   //15회 이상 숙박 시, 17% 할인, 최대 120000만원 할인
    ;

    private final int value;

    Membership(int value) {
        this.value = value;
    }
}
