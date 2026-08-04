package com.mjc.hotel.auth.controller;

import com.mjc.hotel.auth.dto.*;
import com.mjc.hotel.auth.jwt.JwtUtils;
import com.mjc.hotel.auth.service.AuthService;
import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.user.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthRestController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

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
    public ResponseEntity<ApiResponse<UserDto>> signup(@Valid @RequestBody VerifiedSignupRequest request) {
        return ResponseEntity.ok(ApiResponse.make(
                ResponseCode.INSERT_OK,
                "verified signup success",
                this.authService.signup(request)
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokenDto>> emailLogin(@Valid @RequestBody EmailLoginRequest request) {
        Authentication auth = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
//        this.authService.loginWithEmail(request);

        String accessToken = this.jwtUtils.generateAccessToken(request.email());
        String refreshToken = this.jwtUtils.generateRefreshToken(request.password());

        AuthTokenDto authTokenDto = new AuthTokenDto(accessToken, refreshToken);
        return ResponseEntity.status(200).body(
                ApiResponse.make(ResponseCode.SUCCESS,
                        "email login success",
                        authTokenDto)
        );
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<UserDto>> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        return ResponseEntity.ok(ApiResponse.make(
                ResponseCode.SUCCESS,
                "google login success",
                this.authService.loginWithGoogle(request)
        ));
    }
}
