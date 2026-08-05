package com.mjc.hotel.auth.dto;

import com.mjc.hotel.user.dto.UserDto;

public record AuthenticatedUserDto(
        UserDto user,
        String accessToken,
        String refreshToken
) {
}
