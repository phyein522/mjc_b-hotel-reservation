package com.mjc.hotel.sales_analysis.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    // 어떤 예약의 결제인지 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    // PAID 상태만 매출로 집계
    @Column(name = "payment_status", nullable = false, length = 30)
    private String paymentStatus;

    // 실제 결제 금액 (매출 집계에 사용)
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "card_company", length = 100)
    private String cardCompany;

    @Column(name = "card_last4", length = 4)
    private String cardLast4;
}