package com.mjc.hotel.auth.dto;

public record EmailVerifyResponse(
        String verificationToken,
        long expiresInSeconds
) {
}
