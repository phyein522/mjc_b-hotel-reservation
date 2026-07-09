package com.mjc.hotel.user.entity;

import lombok.Getter;

@Getter
public enum Status {    //계정 상태
    ACTIVE(0)   //활성화
    , DORMANT(1)    //휴면
    , LOCKED(2) //잠김
    , WITHDRAW(3)   //탈퇴
    ;

    private final int value;

    Status(int value) {
        this.value = value;
    }
}
