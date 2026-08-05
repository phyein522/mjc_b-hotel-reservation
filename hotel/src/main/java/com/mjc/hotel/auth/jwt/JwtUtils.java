package com.mjc.hotel.auth.jwt;

import com.mjc.hotel.user.dto.IUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {
    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final String secret = "thisismyjwtsecretkey!123456abcdef";
    private final Long expireAccessToken = 1800000L;
    private final Long expireRefreshToken = 604800000L;
    private final SecretKey secretKey;

    public JwtUtils() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(String value) {
        return this.generateToken(value, this.expireAccessToken, ACCESS_TOKEN_TYPE);
    }

    public String generateRefreshToken(String value) {
        return this.generateToken(value, this.expireRefreshToken, REFRESH_TOKEN_TYPE);
    }

    public String generateToken(String value, Long milliSeconds) {
        return this.generateToken(value, milliSeconds, ACCESS_TOKEN_TYPE);
    }

    private String generateToken(String value, Long milliSeconds, String tokenType) {
        return Jwts.builder()
                .subject(value)
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + milliSeconds))
                .signWith(this.secretKey)
                .compact();
    }

    public String generateToken(IUser user, Long milliSeconds) {
        return Jwts.builder()
                .subject(user.getEmail())
                .id(UUID.randomUUID().toString())
                .claim("role", user.getRole())
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + milliSeconds))
                .signWith(this.secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException | IllegalArgumentException exception) {
            throw exception;
        } catch (JwtException exception) {
            throw exception;
        }
    }

    public String getRole(String token) throws JwtExpireException {
        return this.parseToken(token).get("role", String.class);
    }

    public String getValueFromClaims(String token, String key) throws JwtExpireException {
        return this.parseToken(token).get(key, String.class);
    }

    public String getEmail(String token) throws JwtExpireException {
        return this.parseToken(token).getSubject();
    }

    public Boolean validateToken(String token) throws JwtExpireException {
        this.parseToken(token);
        return true;
    }

    public Boolean validateAccessToken(String token) throws JwtExpireException {
        return this.validateTokenType(token, ACCESS_TOKEN_TYPE);
    }

    public Boolean validateRefreshToken(String token) throws JwtExpireException {
        return this.validateTokenType(token, REFRESH_TOKEN_TYPE);
    }

    private Boolean validateTokenType(String token, String expectedType) throws JwtExpireException {
        String actualType = this.parseToken(token).get(TOKEN_TYPE_CLAIM, String.class);
        if (!expectedType.equals(actualType)) {
            throw new JwtIllegalException("Invalid JWT token type");
        }
        return true;
    }

    public String resolveJwtTokenFromBearerToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
