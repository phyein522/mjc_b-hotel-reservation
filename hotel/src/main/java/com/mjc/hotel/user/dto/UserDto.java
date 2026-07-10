package com.mjc.hotel.user.dto;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.user.entity.Membership;
import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.Status;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserDto extends BaseDto implements IUser {
    private Long userId;
    private String email;
    private String password;
    private String name;
    private String phone;
    private Role role;
    private Status status;
    private Membership membership;
    private Boolean marketingAgreed;
    private Integer point;
}
