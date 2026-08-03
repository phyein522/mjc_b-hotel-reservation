package com.mjc.hotel.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verifications", indexes = {
        @Index(name = "idx_email_verifications_email_created", columnList = "email, created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_verification_id")
    private Long emailVerificationId;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "code_hash", nullable = false, length = 64)
    private String codeHash;

    @Column(name = "code_expires_at", nullable = false)
    private LocalDateTime codeExpiresAt;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verification_token_hash", length = 64)
    private String verificationTokenHash;

    @Column(name = "verification_token_expires_at")
    private LocalDateTime verificationTokenExpiresAt;

    @Column(name = "consumed_at")
    private LocalDateTime consumedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
