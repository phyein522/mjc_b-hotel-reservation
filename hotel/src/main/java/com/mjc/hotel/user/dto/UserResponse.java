package com.mjc.hotel.user.dto;

import com.mjc.hotel.user.entity.Membership;
import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.Status;
import com.mjc.hotel.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {

    private Long userId;
    private String email;
    private String name;
    private String phone;
    private Role role;
    private Status status;
    private Membership membership;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    // password는 의도적으로 제외

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .membership(user.getMembership())
                .createdAt(user.getCreatedAt())
                .modifiedAt(user.getModifiedAt())
                .build();
    }
}