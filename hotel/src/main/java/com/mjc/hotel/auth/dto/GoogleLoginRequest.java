package com.mjc.hotel.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @NotBlank(message = "Google 인증 정보가 없습니다.")
        String credential
) {
}
