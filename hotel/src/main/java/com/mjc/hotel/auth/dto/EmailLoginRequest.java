package com.mjc.hotel.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailLoginRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 주소를 입력해주세요.")
        String email,
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
}
