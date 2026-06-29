package com.mjc.hotel.users;

import com.mjc.hotel.users.Role;
import com.mjc.hotel.users.Status;
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

    @Size(max = 10)
    private String socialLogin;

    @Size(max = 30)
    private String socialLoginId;

    @Size(max = 10)
    private String membership;
}
