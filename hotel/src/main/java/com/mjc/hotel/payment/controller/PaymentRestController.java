package com.mjc.hotel.payment.controller;

import com.mjc.hotel.payment.dto.PaymentDto;
import com.mjc.hotel.payment.enums.PaymentStatus;
import com.mjc.hotel.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentRestController {

    private final PaymentService paymentService;

    // GET /api/payments — 전체 조회
    @GetMapping
    public List<PaymentDto> getPayments() {
        return paymentService.findAll();
    }

    // GET /api/payments/{paymentId} — 단건 조회
    @GetMapping("/{paymentId}")
    public PaymentDto getPayment(@PathVariable Long paymentId) {
        return paymentService.findById(paymentId);
    }

    // GET /api/payments/user/{userId} — 사용자별 조회
    @GetMapping("/user/{userId}")
    public List<PaymentDto> getPaymentsByUser(@PathVariable Long userId) {
        return paymentService.findByUserId(userId);
    }

    // GET /api/payments/status/{status} — 상태별 조회
    @GetMapping("/status/{status}")
    public List<PaymentDto> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        return paymentService.findByStatus(status);
    }

    // POST /api/payments — 결제 생성
    @PostMapping
    public PaymentDto insertPayment(@RequestBody PaymentDto dto) {
        return paymentService.insert(dto);
    }

    // PUT /api/payments/{paymentId} — 결제 수정
    @PutMapping("/{paymentId}")
    public PaymentDto updatePayment(@PathVariable Long paymentId, @RequestBody PaymentDto dto) {
        return paymentService.update(paymentId, dto);
    }

    // PATCH /api/payments/{paymentId}/pay — 결제 완료 처리
    @PatchMapping("/{paymentId}/pay")
    public PaymentDto pay(@PathVariable Long paymentId) {
        return paymentService.pay(paymentId);
    }

    // PATCH /api/payments/{paymentId}/cancel — 결제 취소
    @PatchMapping("/{paymentId}/cancel")
    public PaymentDto cancel(@PathVariable Long paymentId) {
        return paymentService.cancel(paymentId);
    }

    // PATCH /api/payments/{paymentId}/refund — 환불 처리
    @PatchMapping("/{paymentId}/refund")
    public PaymentDto refund(@PathVariable Long paymentId) {
        return paymentService.refund(paymentId);
    }

    // DELETE /api/payments/{paymentId} — 삭제
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long paymentId) {
        paymentService.deleteById(paymentId);
        return ResponseEntity.noContent().build();
    }
}