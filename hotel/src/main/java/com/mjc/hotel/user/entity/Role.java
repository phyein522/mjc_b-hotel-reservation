package com.mjc.hotel.user.entity;

import lombok.Getter;

@Getter
public enum Role {  //권한
    CUSTOMER(0) //일반 사용자
    , HOTEL_MANAGER(1)  //호텔 관리자
    , ADMIN(2)  //관리자
    , SUPER_ADMIN(3)    //슈퍼 관리자
    ;

    private final int value;

    Role(int value) {
        this.value = value;
    }
}
