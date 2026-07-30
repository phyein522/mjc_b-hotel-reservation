package com.mjc.hotel.auth;

import com.mjc.hotel.auth.dto.*;
import com.mjc.hotel.auth.entity.EmailVerificationEntity;
import com.mjc.hotel.auth.google.GoogleIdentityVerifier;
import com.mjc.hotel.auth.google.GoogleProfile;
import com.mjc.hotel.auth.repository.EmailVerificationRepository;
import com.mjc.hotel.auth.service.AuthService;
import com.mjc.hotel.user.dto.UserDto;
import com.mjc.hotel.user.entity.Status;
import com.mjc.hotel.user.entity.UserEntity;
import com.mjc.hotel.user.repository.UserRepository;
import com.mjc.hotel.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock EmailVerificationRepository emailVerificationRepository;
    @Mock UserRepository userRepository;
    @Mock UserService userService;
    @Mock JavaMailSender mailSender;
    @Mock GoogleIdentityVerifier googleIdentityVerifier;

    private AuthService authService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.authService = new AuthService(
                this.emailVerificationRepository,
                this.userRepository,
                this.userService,
                this.mailSender,
                this.googleIdentityVerifier,
                this.passwordEncoder,
                "noreply@omnistay.test",
                "test-secret",
                10,
                30,
                60,
                5
        );
    }

    @Test
    void emailLoginAcceptsBcryptPasswordAndHidesPassword() {
        UserEntity user = UserEntity.builder()
                .userId(3L)
                .email("user@example.com")
                .password(this.passwordEncoder.encode("password"))
                .status(Status.ACTIVE)
                .build();
        when(this.userRepository.findByEmailIgnoreCase("user@example.com"))
                .thenReturn(Optional.of(user));

        UserDto result = this.authService.loginWithEmail(
                new EmailLoginRequest(" User@Example.com ", "password")
        );

        assertThat(result.getUserId()).isEqualTo(3L);
        assertThat(result.getPassword()).isNull();
        verify(this.userRepository, never()).save(any());
    }

    @Test
    void emailLoginUpgradesLegacyPlaintextPassword() {
        UserEntity user = UserEntity.builder()
                .userId(3L)
                .email("user@example.com")
                .password("legacy-password")
                .status(Status.ACTIVE)
                .build();
        when(this.userRepository.findByEmailIgnoreCase("user@example.com"))
                .thenReturn(Optional.of(user));
        when(this.userRepository.save(user)).thenReturn(user);

        this.authService.loginWithEmail(
                new EmailLoginRequest("user@example.com", "legacy-password")
        );

        assertThat(user.getPassword()).isNotEqualTo("legacy-password");
        assertThat(this.passwordEncoder.matches("legacy-password", user.getPassword())).isTrue();
        verify(this.userRepository).save(user);
    }

    @Test
    void emailLoginRejectsWrongPassword() {
        UserEntity user = UserEntity.builder()
                .email("user@example.com")
                .password(this.passwordEncoder.encode("correct-password"))
                .status(Status.ACTIVE)
                .build();
        when(this.userRepository.findByEmailIgnoreCase("user@example.com"))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> this.authService.loginWithEmail(
                new EmailLoginRequest("user@example.com", "wrong-password")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    @Test
    void sendEmailCodeSendsSixDigitCodeAndStoresHash() {
        when(this.userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(false);
        when(this.emailVerificationRepository.findTopByEmailOrderByCreatedAtDesc("user@example.com"))
                .thenReturn(Optional.empty());

        this.authService.sendEmailCode(new EmailCodeRequest(" User@Example.com "));

        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(this.mailSender).send(mailCaptor.capture());
        assertThat(mailCaptor.getValue().getText()).containsPattern("\\d{6}");

        ArgumentCaptor<EmailVerificationEntity> entityCaptor =
                ArgumentCaptor.forClass(EmailVerificationEntity.class);
        verify(this.emailVerificationRepository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getEmail()).isEqualTo("user@example.com");
        assertThat(entityCaptor.getValue().getCodeHash()).hasSize(64);
        assertThat(entityCaptor.getValue().getCodeHash()).doesNotContainPattern("\\b\\d{6}\\b");
    }

    @Test
    void sendEmailCodeWithoutMailConfigurationUsesClientErrorInsteadOfServiceUnavailable() {
        AuthService serviceWithoutMail = new AuthService(
                this.emailVerificationRepository,
                this.userRepository,
                this.userService,
                this.mailSender,
                this.googleIdentityVerifier,
                this.passwordEncoder,
                "",
                "test-secret",
                10,
                30,
                60,
                5
        );
        when(this.userRepository.existsByEmailIgnoreCase("user@naver.com")).thenReturn(false);

        assertThatThrownBy(() -> serviceWithoutMail.sendEmailCode(
                new EmailCodeRequest("user@naver.com")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("일반 이메일 회원가입");
        verify(this.mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void verifyEmailCodeRejectsExpiredCode() {
        EmailVerificationEntity expired = EmailVerificationEntity.builder()
                .email("user@example.com")
                .codeHash("unused")
                .codeExpiresAt(LocalDateTime.now().minusSeconds(1))
                .attemptCount(0)
                .createdAt(LocalDateTime.now().minusMinutes(11))
                .build();
        when(this.emailVerificationRepository.findTopByEmailOrderByCreatedAtDesc("user@example.com"))
                .thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> this.authService.verifyEmailCode(
                new EmailVerifyRequest("user@example.com", "123456")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("만료");
    }

    @Test
    void googleLoginCreatesUserFromVerifiedGoogleProfile() {
        GoogleProfile profile = new GoogleProfile(
                "google-subject",
                "user@example.com",
                "테스트 사용자",
                true,
                false
        );
        when(this.googleIdentityVerifier.verify("credential")).thenReturn(profile);
        when(this.userRepository.findByGoogleSubject("google-subject")).thenReturn(Optional.empty());
        when(this.userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.empty());
        when(this.userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setUserId(7L);
            return entity;
        });

        UserDto result = this.authService.loginWithGoogle(new GoogleLoginRequest("credential"));

        assertThat(result.getUserId()).isEqualTo(7L);
        assertThat(result.getEmail()).isEqualTo("user@example.com");
        assertThat(result.getPassword()).isNull();
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(this.userRepository).save(captor.capture());
        assertThat(captor.getValue().getGoogleSubject()).isEqualTo("google-subject");
        assertThat(captor.getValue().getEmailVerified()).isTrue();
    }

    @Test
    void googleLoginLinksExistingAuthoritativeGoogleEmailAccount() {
        GoogleProfile profile = new GoogleProfile(
                "google-subject",
                "user@gmail.com",
                "테스트 사용자",
                true,
                true
        );
        UserEntity existingUser = UserEntity.builder()
                .userId(9L)
                .email("user@gmail.com")
                .password(this.passwordEncoder.encode("password"))
                .status(Status.ACTIVE)
                .emailVerified(true)
                .build();
        when(this.googleIdentityVerifier.verify("credential")).thenReturn(profile);
        when(this.userRepository.findByGoogleSubject("google-subject")).thenReturn(Optional.empty());
        when(this.userRepository.findByEmailIgnoreCase("user@gmail.com"))
                .thenReturn(Optional.of(existingUser));
        when(this.userRepository.save(existingUser)).thenReturn(existingUser);

        UserDto result = this.authService.loginWithGoogle(new GoogleLoginRequest("credential"));

        assertThat(result.getUserId()).isEqualTo(9L);
        assertThat(existingUser.getGoogleSubject()).isEqualTo("google-subject");
        assertThat(existingUser.getEmailVerified()).isTrue();
        verify(this.userRepository).save(existingUser);
    }

    @Test
    void googleLoginDoesNotAutoLinkNonAuthoritativeThirdPartyEmail() {
        GoogleProfile profile = new GoogleProfile(
                "google-subject",
                "user@naver.com",
                "테스트 사용자",
                true,
                false
        );
        when(this.googleIdentityVerifier.verify("credential")).thenReturn(profile);
        when(this.userRepository.findByGoogleSubject("google-subject")).thenReturn(Optional.empty());
        when(this.userRepository.findByEmailIgnoreCase("user@naver.com"))
                .thenReturn(Optional.of(UserEntity.builder().email("user@naver.com").build()));

        assertThatThrownBy(() -> this.authService.loginWithGoogle(new GoogleLoginRequest("credential")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이메일과 비밀번호");
        verify(this.userRepository, never()).save(any());
    }
}
