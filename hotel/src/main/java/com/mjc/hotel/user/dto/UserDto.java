package com.mjc.hotel.user.dto;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.user.entity.Membership;
import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserDto extends BaseDto implements IUser {

    private Long userId;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 255)
    private String email;

    // 응답 시 직렬화 제외: @JsonIgnore 사용 (필요 시 주석 해제)
    // @JsonIgnore
    @Size(max = 255)
    private String password;

    @Size(max = 100)
    private String name;

    @Size(max = 30)
    private String phone;

    private Role role;

    private Status status;

    private Membership membership;

    @Size(max = 10)
    private String socialLogin;

    @Size(max = 30)
    private String socialLoginId;
}