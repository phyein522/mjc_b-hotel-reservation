package com.mjc.hotel.auth.dto;

public record AuthConfigResponse(
        boolean emailVerificationEnabled,
        boolean googleLoginEnabled,
        String googleClientId
) {
}
