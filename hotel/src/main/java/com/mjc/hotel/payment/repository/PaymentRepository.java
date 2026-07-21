package com.mjc.hotel.payment.repository;

import com.mjc.hotel.bookings.BookingEntity;
import com.mjc.hotel.payment.dto.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByPaymentId(Long paymentId);
    Optional<PaymentEntity> findByBookingEquals(BookingEntity booking);
    Optional<PaymentEntity> findByPaymentKey(String paymentKey);
    Optional<PaymentEntity> findByOrderId(String orderId);
}
