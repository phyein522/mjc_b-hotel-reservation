package com.mjc.hotel.payment.repository;

import com.mjc.hotel.payment.entity.PaymentEntity;
import com.mjc.hotel.payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    // 사용자별 결제 내역
    List<PaymentEntity> findByUserUserId(Long userId);

    // 객실별 결제 내역
    List<PaymentEntity> findByRoomRoomId(Long roomId);

    // 상태별 결제 내역
    List<PaymentEntity> findByPaymentStatus(PaymentStatus paymentStatus);

    // 사용자 + 상태별 조회
    List<PaymentEntity> findByUserUserIdAndPaymentStatus(Long userId, PaymentStatus paymentStatus);
}