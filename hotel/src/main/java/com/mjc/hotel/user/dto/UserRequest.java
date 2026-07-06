package com.mjc.hotel.user.dto;

import com.mjc.hotel.user.entity.Membership;
import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.Status;
import com.mjc.hotel.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 255)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(max = 255)
    private String password;

    @Size(max = 100)
    private String name;

    @Size(max = 30)
    private String phone;

    @NotNull(message = "역할은 필수입니다.")
    private Role role;

    @NotNull(message = "상태는 필수입니다.")
    private Status status;

    private Membership membership;

    public User toEntity() {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .phone(phone)
                .role(role)
                .status(status)
                .membership(membership)
                .build();
    }
}