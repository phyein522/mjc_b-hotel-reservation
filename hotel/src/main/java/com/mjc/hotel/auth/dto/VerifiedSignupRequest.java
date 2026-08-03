package com.mjc.hotel.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifiedSignupRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,
        @NotBlank(message = "이메일 인증을 완료해주세요.")
        String verificationToken,
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 4, max = 100, message = "비밀번호는 4자 이상 100자 이하로 입력해주세요.")
        String password,
        @NotBlank(message = "이름을 입력해주세요.")
        @Size(max = 100, message = "이름은 100자 이하로 입력해주세요.")
        String name,
        @NotBlank(message = "전화번호를 입력해주세요.")
        @Size(max = 30, message = "전화번호는 30자 이하로 입력해주세요.")
        String phone
) {
}
