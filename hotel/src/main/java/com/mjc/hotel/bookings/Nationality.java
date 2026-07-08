package com.mjc.hotel.bookings;

import lombok.Getter;

@Getter
public enum Nationality {
    OTHER(0)   //그 외
    , KOREA(1)   //한국
    , US(2)    //미국
    , CHINA(3) //중국
    , JAPAN(4) //일본
    , TAIWAN(5)    //대만
    , HONG_KONG(6) //홍콩
    , PHILIPPINES(7)   //필리핀
    , VIETNAM(8)   //베트남
    , SINGAPORE(9) //싱가포르
    ;

    private final int value;

    Nationality(int value) {
        this.value = value;
    }
}
