package com.mjc.hotel.auth.controller;

import com.mjc.hotel.auth.dto.*;
import com.mjc.hotel.auth.jwt.JwtUtils;
import com.mjc.hotel.auth.service.AuthService;
import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.user.dto.UserDto;
import com.mjc.hotel.user.entity.Status;
import com.mjc.hotel.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthRestController {
    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @GetMapping("/config")
    public ResponseEntity<ApiResponse<AuthConfigResponse>> config() {
        return ResponseEntity.ok(ApiResponse.make(
                ResponseCode.SELECT_OK,
                "auth config",
                this.authService.getConfig()
        ));
    }

    @PostMapping("/email/send-code")
    public ResponseEntity<ApiResponse<Void>> sendEmailCode(@Valid @RequestBody EmailCodeRequest request) {
        this.authService.sendEmailCode(request);
        return ResponseEntity.ok(ApiResponse.make(
                ResponseCode.SUCCESS,
                "verification email sent",
                null
        ));
    }

    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<EmailVerifyResponse>> verifyEmailCode(
            @Valid @RequestBody EmailVerifyRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.make(
                ResponseCode.SUCCESS,
                "email verified",
                this.authService.verifyEmailCode(request)
        ));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthenticatedUserDto>> signup(@Valid @RequestBody VerifiedSignupRequest request) {
        UserDto user = this.authService.signup(request);
        return ResponseEntity.ok(ApiResponse.make(
                ResponseCode.INSERT_OK,
                "verified signup success",
                createSession(user)
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticatedUserDto>> emailLogin(@Valid @RequestBody EmailLoginRequest request) {
        UserDto user = this.authService.loginWithEmail(request);
        return ResponseEntity.ok(ApiResponse.make(
                ResponseCode.SUCCESS,
                "email login success",
                createSession(user)
        ));
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthenticatedUserDto>> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        UserDto user = this.authService.loginWithGoogle(request);
        return ResponseEntity.ok(ApiResponse.make(
                ResponseCode.SUCCESS,
                "google login success",
                createSession(user)
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthTokenDto>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            this.jwtUtils.validateRefreshToken(request.refreshToken());
            String email = this.jwtUtils.getEmail(request.refreshToken());
            UserDto user = this.userService.findByEmail(email);
            if (user.getStatus() != Status.ACTIVE) {
                throw new IllegalStateException("User is not active");
            }

            AuthTokenDto tokens = new AuthTokenDto(
                    this.jwtUtils.generateAccessToken(email),
                    this.jwtUtils.generateRefreshToken(email)
            );
            return ResponseEntity.ok(ApiResponse.make(
                    ResponseCode.SUCCESS,
                    "token refresh success",
                    tokens
            ));
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.make(
                    ResponseCode.AUTHENTICATION_ERROR,
                    "refresh token is invalid or expired",
                    null
            ));
        }
    }

    private AuthenticatedUserDto createSession(UserDto user) {
        String email = user.getEmail();
        return new AuthenticatedUserDto(
                user,
                this.jwtUtils.generateAccessToken(email),
                this.jwtUtils.generateRefreshToken(email)
        );
    }
}
