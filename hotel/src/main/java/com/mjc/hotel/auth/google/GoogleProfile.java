package com.mjc.hotel.auth.google;

public record GoogleProfile(
        String subject,
        String email,
        String name,
        boolean emailVerified,
        boolean emailAuthoritative
) {
}
