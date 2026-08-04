package com.mjc.hotel.auth.service;

import com.mjc.hotel.auth.dto.*;
import com.mjc.hotel.auth.entity.EmailVerificationEntity;
import com.mjc.hotel.auth.exception.AuthenticationServiceException;
import com.mjc.hotel.auth.google.GoogleIdentityVerifier;
import com.mjc.hotel.auth.google.GoogleProfile;
import com.mjc.hotel.auth.repository.EmailVerificationRepository;
import com.mjc.hotel.user.dto.UserDto;
import com.mjc.hotel.user.entity.*;
import com.mjc.hotel.user.exception.DuplicateEmailException;
import com.mjc.hotel.user.repository.UserRepository;
import com.mjc.hotel.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
public class AuthService {
    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JavaMailSender mailSender;
    private final GoogleIdentityVerifier googleIdentityVerifier;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    private final String mailFrom;
    private final String hashSecret;
    private final int codeExpirationMinutes;
    private final int tokenExpirationMinutes;
    private final int resendCooldownSeconds;
    private final int maxAttempts;

    public AuthService(
            EmailVerificationRepository emailVerificationRepository,
            UserRepository userRepository,
            UserService userService,
            JavaMailSender mailSender,
            GoogleIdentityVerifier googleIdentityVerifier,
            PasswordEncoder passwordEncoder,
            @Value("${auth.email.from:}") String mailFrom,
            @Value("${auth.email.hash-secret}") String hashSecret,
            @Value("${auth.email.code-expiration-minutes:10}") int codeExpirationMinutes,
            @Value("${auth.email.token-expiration-minutes:30}") int tokenExpirationMinutes,
            @Value("${auth.email.resend-cooldown-seconds:60}") int resendCooldownSeconds,
            @Value("${auth.email.max-attempts:5}") int maxAttempts
    ) {
        this.emailVerificationRepository = emailVerificationRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailSender = mailSender;
        this.googleIdentityVerifier = googleIdentityVerifier;
        this.passwordEncoder = passwordEncoder;
        this.mailFrom = mailFrom == null ? "" : mailFrom.trim();
        this.hashSecret = hashSecret;
        this.codeExpirationMinutes = codeExpirationMinutes;
        this.tokenExpirationMinutes = tokenExpirationMinutes;
        this.resendCooldownSeconds = resendCooldownSeconds;
        this.maxAttempts = maxAttempts;
    }

//    @Transactional
    public void sendEmailCode(EmailCodeRequest request) {
        String email = normalizeEmail(request.email());
        if (this.userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException("이미 가입된 이메일입니다.");
        }
        if (!StringUtils.hasText(this.mailFrom)) {
            throw new IllegalArgumentException(
                    "메일 발신 설정이 없어 인증번호를 보낼 수 없습니다. 일반 이메일 회원가입을 이용해주세요."
            );
        }

        LocalDateTime now = LocalDateTime.now();
        this.emailVerificationRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .filter(latest -> latest.getCreatedAt().plusSeconds(this.resendCooldownSeconds).isAfter(now))
                .ifPresent(latest -> {
                    long remaining = Duration.between(now, latest.getCreatedAt().plusSeconds(this.resendCooldownSeconds)).toSeconds() + 1;
                    throw new IllegalArgumentException(remaining + "초 후에 인증번호를 다시 요청할 수 있습니다.");
                });

        String code = String.format("%06d", this.secureRandom.nextInt(1_000_000));
        log.info("email code: {}", code);
        EmailVerificationEntity verification = EmailVerificationEntity.builder()
                .email(email)
                .codeHash(hash(email, code))
                .codeExpiresAt(now.plusMinutes(this.codeExpirationMinutes))
                .attemptCount(0)
                .createdAt(now)
                .build();

        this.emailVerificationRepository.save(verification);
        sendVerificationMail(email, code);
    }

    @Transactional
    public EmailVerifyResponse verifyEmailCode(EmailVerifyRequest request) {
        String email = normalizeEmail(request.email());
        EmailVerificationEntity verification = this.emailVerificationRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new IllegalArgumentException("먼저 인증번호를 요청해주세요."));
        LocalDateTime now = LocalDateTime.now();

        if (verification.getConsumedAt() != null) {
            throw new IllegalArgumentException("이미 사용된 이메일 인증입니다.");
        }
        if (verification.getCodeExpiresAt().isBefore(now)) {
            throw new IllegalArgumentException("인증번호가 만료되었습니다. 새 인증번호를 요청해주세요.");
        }
        if (verification.getAttemptCount() >= this.maxAttempts) {
            throw new IllegalArgumentException("인증번호 확인 횟수를 초과했습니다. 새 인증번호를 요청해주세요.");
        }

        verification.setAttemptCount(verification.getAttemptCount() + 1);
        if (!MessageDigest.isEqual(
                verification.getCodeHash().getBytes(StandardCharsets.UTF_8),
                hash(email, request.code()).getBytes(StandardCharsets.UTF_8)
        )) {
            this.emailVerificationRepository.save(verification);
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        String verificationToken = UUID.randomUUID().toString();
        verification.setVerifiedAt(now);
        verification.setVerificationTokenHash(hash(email, verificationToken));
        verification.setVerificationTokenExpiresAt(now.plusMinutes(this.tokenExpirationMinutes));
        this.emailVerificationRepository.save(verification);
        return new EmailVerifyResponse(verificationToken, Duration.ofMinutes(this.tokenExpirationMinutes).toSeconds());
    }

    @Transactional
    public UserDto signup(VerifiedSignupRequest request) {
        String email = normalizeEmail(request.email());
        EmailVerificationEntity verification = this.emailVerificationRepository
                .findTopByEmailAndVerificationTokenHashOrderByCreatedAtDesc(
                        email,
                        hash(email, request.verificationToken())
                )
                .orElseThrow(() -> new IllegalArgumentException("이메일 인증 정보가 올바르지 않습니다."));
        LocalDateTime now = LocalDateTime.now();

//        if (verification.getVerifiedAt() == null || verification.getConsumedAt() != null) {
//            throw new IllegalArgumentException("사용할 수 없는 이메일 인증 정보입니다.");
//        }
        if (verification.getVerificationTokenExpiresAt() == null
                || verification.getVerificationTokenExpiresAt().isBefore(now)) {
            throw new IllegalArgumentException("이메일 인증이 만료되었습니다. 다시 인증해주세요.");
        }

        UserDto signup = UserDto.builder()
                .email(email)
                .password(request.password())
                .name(request.name().trim())
                .phone(request.phone().trim())
                .marketingAgreed(false)
                .build();
        UserDto saved = this.userService.insert(signup);
        UserEntity savedEntity = this.userRepository.findById(saved.getUserId()).orElseThrow();
        savedEntity.setEmailVerified(true);
        this.userRepository.save(savedEntity);
        verification.setConsumedAt(now);
        this.emailVerificationRepository.save(verification);
        return sanitize(savedEntity);
    }

    @Transactional
    public UserDto loginWithEmail(EmailLoginRequest request) {
        String email = normalizeEmail(request.email());
        UserEntity user = this.userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(this::invalidCredentials);

        String storedPassword = user.getPassword();
        boolean bcryptPassword = isBcryptPassword(storedPassword);
        boolean passwordMatches = bcryptPassword
                ? this.passwordEncoder.matches(request.password(), storedPassword)
                : constantTimeEquals(request.password(), storedPassword);
        if (!passwordMatches) {
            throw invalidCredentials();
        }
        if (user.getStatus() != Status.ACTIVE) {
            throw new IllegalArgumentException("현재 로그인할 수 없는 계정입니다.");
        }

        if (!bcryptPassword) {
            user.setPassword(this.passwordEncoder.encode(request.password()));
            user = this.userRepository.save(user);
        }
        return sanitize(user);
    }

    @Transactional
    public UserDto loginWithGoogle(GoogleLoginRequest request) {
        GoogleProfile profile;
        try {
            profile = this.googleIdentityVerifier.verify(request.credential());
        } catch (IllegalStateException ex) {
            throw new AuthenticationServiceException(ex.getMessage(), ex);
        }

        UserEntity user = this.userRepository.findByGoogleSubject(profile.subject()).orElse(null);
        if (user != null) {
            user.setEmailVerified(true);
            return sanitize(this.userRepository.save(user));
        }

        UserEntity emailUser = this.userRepository.findByEmailIgnoreCase(profile.email()).orElse(null);
        if (emailUser != null) {
            if (!profile.emailAuthoritative()) {
                throw new IllegalArgumentException(
                        "이 이메일은 기존 이메일과 비밀번호로 로그인해주세요."
                );
            }
            if (StringUtils.hasText(emailUser.getGoogleSubject())
                    && !emailUser.getGoogleSubject().equals(profile.subject())) {
                throw new IllegalArgumentException("이미 다른 Google 계정과 연결된 이메일입니다.");
            }
            if (emailUser.getStatus() != Status.ACTIVE) {
                throw new IllegalArgumentException("현재 로그인할 수 없는 계정입니다.");
            }
            emailUser.setGoogleSubject(profile.subject());
            emailUser.setEmailVerified(true);
            return sanitize(this.userRepository.save(emailUser));
        }

        UserEntity newUser = UserEntity.builder()
                .email(profile.email())
                .password(this.passwordEncoder.encode(UUID.randomUUID().toString()))
                .name(profile.name())
                .phone("")
                .role(Role.CUSTOMER)
                .status(Status.ACTIVE)
                .membership(Membership.NEW_MEMBER)
                .marketingAgreed(false)
                .point(0)
                .emailVerified(profile.emailVerified())
                .googleSubject(profile.subject())
                .build();
        return sanitize(this.userRepository.save(newUser));
    }

    public AuthConfigResponse getConfig() {
        return new AuthConfigResponse(
                StringUtils.hasText(this.mailFrom),
                this.googleIdentityVerifier.isConfigured(),
                this.googleIdentityVerifier.getClientId()
        );
    }

    private void sendVerificationMail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.mailFrom);
        message.setTo(email);
        message.setSubject("[OmniStay] 이메일 인증번호");
        message.setText("""
                OmniStay 회원가입 인증번호는 %s 입니다.

                인증번호는 %d분 동안 유효합니다.
                본인이 요청하지 않았다면 이 메일을 무시해주세요.
                """.formatted(code, this.codeExpirationMinutes));
        try {
            this.mailSender.send(message);
        } catch (MailException ex) {
            throw new AuthenticationServiceException("인증 메일을 전송하지 못했습니다. 메일 서버 설정을 확인해주세요.", ex);
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private boolean isBcryptPassword(String password) {
        return password != null && password.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$");
    }

    private boolean constantTimeEquals(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        return MessageDigest.isEqual(
                rawPassword.getBytes(StandardCharsets.UTF_8),
                storedPassword.getBytes(StandardCharsets.UTF_8)
        );
    }

    private IllegalArgumentException invalidCredentials() {
        return new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    private String hash(String email, String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((this.hashSecret + ":" + email + ":" + value).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256을 사용할 수 없습니다.", ex);
        }
    }

    private UserDto sanitize(UserEntity entity) {
        UserDto result = (UserDto) new UserDto().copyMembers(entity, true);
        result.setPassword(null);
        return result;
    }
}
