package com.mjc.hotel.payment.controller;

import com.mjc.hotel.payment.dto.RefundCreateRequest;
import com.mjc.hotel.payment.dto.RefundResponse;
import com.mjc.hotel.payment.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping
    public ResponseEntity<RefundResponse> requestRefund(@RequestBody RefundCreateRequest request) {
        RefundResponse response = refundService.requestRefund(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{refundId}/process")
    public ResponseEntity<RefundResponse> processRefund(@PathVariable Long refundId) {
        RefundResponse response = refundService.processRefund(refundId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{refundId}")
    public ResponseEntity<RefundResponse> getRefund(@PathVariable Long refundId) {
        RefundResponse response = refundService.getRefund(refundId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<RefundResponse>> getRefundsByPaymentId(@PathVariable Long paymentId) {
        List<RefundResponse> responses = refundService.getRefundsByPaymentId(paymentId);
        return ResponseEntity.ok(responses);
    }
}
