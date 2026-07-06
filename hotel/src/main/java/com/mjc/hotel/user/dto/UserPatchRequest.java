package com.mjc.hotel.user.dto;

import com.mjc.hotel.user.entity.Membership;
import com.mjc.hotel.user.entity.Status;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPatchRequest {

    // null이면 변경 안 함

    @Size(max = 100)
    private String name;

    @Size(max = 30)
    private String phone;

    private Status status;

    private Membership membership;
}