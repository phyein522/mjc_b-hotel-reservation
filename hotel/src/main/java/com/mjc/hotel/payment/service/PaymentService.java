package com.mjc.hotel.payment.service;

import com.mjc.hotel.payment.dto.IPayment;
import com.mjc.hotel.payment.dto.PaymentDto;
import com.mjc.hotel.payment.entity.PaymentEntity;
import com.mjc.hotel.payment.enums.PaymentStatus;
import com.mjc.hotel.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // 전체 조회
    public List<PaymentDto> findAll() {
        return paymentRepository.findAll()
                .stream()
                .map(e -> (PaymentDto) new PaymentDto().copyMembers(e, true))
                .toList();
    }

    // 단건 조회
    public PaymentDto findById(Long paymentId) {
        PaymentEntity entity = findEntityOrThrow(paymentId);
        return (PaymentDto) new PaymentDto().copyMembers(entity, true);
    }

    // 사용자별 조회
    public List<PaymentDto> findByUserId(Long userId) {
        return paymentRepository.findByUserUserId(userId)
                .stream()
                .map(e -> (PaymentDto) new PaymentDto().copyMembers(e, true))
                .toList();
    }

    // 상태별 조회
    public List<PaymentDto> findByStatus(PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status)
                .stream()
                .map(e -> (PaymentDto) new PaymentDto().copyMembers(e, true))
                .toList();
    }

    // 결제 생성
    @Transactional
    public PaymentDto insert(IPayment dto) {
        PaymentEntity entity = (PaymentEntity) new PaymentEntity().copyMembers(dto, true);
        entity.setPaymentId(null);

        // 기본 상태 설정
        if (entity.getPaymentStatus() == null) {
            entity.setPaymentStatus(PaymentStatus.PENDING);
        }

        PaymentEntity saved = paymentRepository.save(entity);
        return (PaymentDto) new PaymentDto().copyMembers(saved, true);
    }

    // 결제 수정
    @Transactional
    public PaymentDto update(Long paymentId, IPayment dto) {
        PaymentDto found = findById(paymentId);
        found.copyMembers(dto, false);

        PaymentEntity entity = (PaymentEntity) new PaymentEntity().copyMembers(found, true);
        PaymentEntity saved = paymentRepository.save(entity);
        return (PaymentDto) new PaymentDto().copyMembers(saved, true);
    }

    // 결제 완료 처리
    @Transactional
    public PaymentDto pay(Long paymentId) {
        PaymentDto found = findById(paymentId);
        found.setPaymentStatus(PaymentStatus.PAID);
        found.setPaidAt(LocalDateTime.now());

        PaymentEntity entity = (PaymentEntity) new PaymentEntity().copyMembers(found, true);
        PaymentEntity saved = paymentRepository.save(entity);
        return (PaymentDto) new PaymentDto().copyMembers(saved, true);
    }

    // 결제 취소
    @Transactional
    public PaymentDto cancel(Long paymentId) {
        PaymentDto found = findById(paymentId);

        if (found.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw new IllegalStateException("이미 환불된 결제입니다.");
        }

        found.setPaymentStatus(PaymentStatus.CANCELLED);
        found.setCancelledAt(LocalDateTime.now());

        PaymentEntity entity = (PaymentEntity) new PaymentEntity().copyMembers(found, true);
        PaymentEntity saved = paymentRepository.save(entity);
        return (PaymentDto) new PaymentDto().copyMembers(saved, true);
    }

    // 환불 처리
    @Transactional
    public PaymentDto refund(Long paymentId) {
        PaymentDto found = findById(paymentId);

        if (found.getPaymentStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException("결제 완료 상태에서만 환불이 가능합니다.");
        }

        found.setPaymentStatus(PaymentStatus.REFUNDED);
        found.setCancelledAt(LocalDateTime.now());

        PaymentEntity entity = (PaymentEntity) new PaymentEntity().copyMembers(found, true);
        PaymentEntity saved = paymentRepository.save(entity);
        return (PaymentDto) new PaymentDto().copyMembers(saved, true);
    }

    // 삭제
    @Transactional
    public void deleteById(Long paymentId) {
        if (!paymentRepository.existsById(paymentId)) {
            throw new IllegalArgumentException("결제 정보를 찾을 수 없습니다. ID: " + paymentId);
        }
        paymentRepository.deleteById(paymentId);
    }

    private PaymentEntity findEntityOrThrow(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다. ID: " + paymentId));
    }
}