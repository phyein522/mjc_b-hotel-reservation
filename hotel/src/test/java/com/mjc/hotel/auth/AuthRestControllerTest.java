package com.mjc.hotel.auth;

import com.mjc.hotel.auth.controller.AuthRestController;
import com.mjc.hotel.auth.dto.*;
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
    void verifiedSignupReturnsSanitizedUser() throws Exception {
        UserDto user = UserDto.builder().userId(4L).email("user@example.com").name("사용자").build();
        when(this.authService.signup(any(VerifiedSignupRequest.class))).thenReturn(user);
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
                .andExpect(jsonPath("$.responseData.userId").value(4))
                .andExpect(jsonPath("$.responseData.email").value("user@example.com"));
        verify(this.authService).signup(any(VerifiedSignupRequest.class));
    }

    @Test
    void emailLoginReturnsSanitizedUser() throws Exception {
        UserDto user = UserDto.builder()
                .userId(4L)
                .email("user@example.com")
                .name("User")
                .build();
        when(this.authService.loginWithEmail(any(EmailLoginRequest.class))).thenReturn(user);

        this.mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"user@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.userId").value(4))
                .andExpect(jsonPath("$.responseData.email").value("user@example.com"))
                .andExpect(jsonPath("$.responseData.password").doesNotExist());
        verify(this.authService).loginWithEmail(any(EmailLoginRequest.class));
    }
}
