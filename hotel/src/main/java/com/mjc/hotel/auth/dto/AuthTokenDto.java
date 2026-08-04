package com.mjc.hotel.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class AuthTokenDto {
    private String accessToken;
    private String refreshToken;
}
