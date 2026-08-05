package com.mjc.hotel.auth;

import com.mjc.hotel.auth.controller.AuthRestController;
import com.mjc.hotel.auth.dto.*;
import com.mjc.hotel.auth.jwt.JwtUtils;
import com.mjc.hotel.auth.service.AuthService;
import com.mjc.hotel.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthRestController.class)
class AuthRestControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean AuthService authService;
    @MockitoBean JwtUtils jwtUtils;

    @Test
    void configReturnsFrontendAuthenticationSettings() throws Exception {
        when(this.authService.getConfig()).thenReturn(new AuthConfigResponse(true, true, "client-id"));

        this.mockMvc.perform(get("/api/auth/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.emailVerificationEnabled").value(true))
                .andExpect(jsonPath("$.responseData.googleLoginEnabled").value(true))
                .andExpect(jsonPath("$.responseData.googleClientId").value("client-id"));
    }

    @Test
    void sendEmailCodeValidatesEmail() throws Exception {
        this.mockMvc.perform(post("/api/auth/email/send-code")
                        .contentType("application/json")
                        .content("{\"email\":\"invalid\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifyEmailCodeReturnsSignupToken() throws Exception {
        when(this.authService.verifyEmailCode(any(EmailVerifyRequest.class)))
                .thenReturn(new EmailVerifyResponse("verification-token", 1800));

        this.mockMvc.perform(post("/api/auth/email/verify")
                        .contentType("application/json")
                        .content("{\"email\":\"user@example.com\",\"code\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.verificationToken").value("verification-token"))
                .andExpect(jsonPath("$.responseData.expiresInSeconds").value(1800));
    }

    @Test
    void verifiedSignupReturnsUserAndJwtTokens() throws Exception {
        UserDto user = UserDto.builder().userId(4L).email("user@example.com").name("사용자").build();
        when(this.authService.signup(any(VerifiedSignupRequest.class))).thenReturn(user);
        when(this.jwtUtils.generateAccessToken("user@example.com")).thenReturn("access-token");
        when(this.jwtUtils.generateRefreshToken("user@example.com")).thenReturn("refresh-token");
        VerifiedSignupRequest request = new VerifiedSignupRequest(
                "user@example.com",
                "verification-token",
                "password",
                "사용자",
                "010-1234-5678"
        );

        this.mockMvc.perform(post("/api/auth/signup")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.user.userId").value(4))
                .andExpect(jsonPath("$.responseData.user.email").value("user@example.com"))
                .andExpect(jsonPath("$.responseData.accessToken").value("access-token"))
                .andExpect(jsonPath("$.responseData.refreshToken").value("refresh-token"));
        verify(this.authService).signup(any(VerifiedSignupRequest.class));
    }

    @Test
    void emailLoginReturnsUserAndJwtTokens() throws Exception {
        UserDto user = UserDto.builder()
                .userId(4L)
                .email("user@example.com")
                .name("User")
                .build();
        when(this.authService.loginWithEmail(any(EmailLoginRequest.class))).thenReturn(user);
        when(this.jwtUtils.generateAccessToken("user@example.com")).thenReturn("access-token");
        when(this.jwtUtils.generateRefreshToken("user@example.com")).thenReturn("refresh-token");

        this.mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"user@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.user.userId").value(4))
                .andExpect(jsonPath("$.responseData.user.email").value("user@example.com"))
                .andExpect(jsonPath("$.responseData.user.password").doesNotExist())
                .andExpect(jsonPath("$.responseData.accessToken").value("access-token"))
                .andExpect(jsonPath("$.responseData.refreshToken").value("refresh-token"));
        verify(this.authService).loginWithEmail(any(EmailLoginRequest.class));
    }

    @Test
    void googleLoginReturnsUserAndJwtTokens() throws Exception {
        UserDto user = UserDto.builder().userId(7L).email("google@example.com").name("Google User").build();
        when(this.authService.loginWithGoogle(any(GoogleLoginRequest.class))).thenReturn(user);
        when(this.jwtUtils.generateAccessToken("google@example.com")).thenReturn("google-access-token");
        when(this.jwtUtils.generateRefreshToken("google@example.com")).thenReturn("google-refresh-token");

        this.mockMvc.perform(post("/api/auth/google")
                        .contentType("application/json")
                        .content("{\"credential\":\"google-id-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.user.userId").value(7))
                .andExpect(jsonPath("$.responseData.accessToken").value("google-access-token"))
                .andExpect(jsonPath("$.responseData.refreshToken").value("google-refresh-token"));
    }
}
