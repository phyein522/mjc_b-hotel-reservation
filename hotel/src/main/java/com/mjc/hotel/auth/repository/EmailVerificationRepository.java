package com.mjc.hotel.auth.repository;

import com.mjc.hotel.auth.entity.EmailVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerificationEntity, Long> {
    Optional<EmailVerificationEntity> findTopByEmailOrderByCreatedAtDesc(String email);

    Optional<EmailVerificationEntity> findTopByEmailAndVerificationTokenHashOrderByCreatedAtDesc(
            String email,
            String verificationTokenHash
    );
}
