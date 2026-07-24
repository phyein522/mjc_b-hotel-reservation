package com.mjc.hotel.payment.dto;

import com.mjc.hotel.bookings.BookingDto;
import com.mjc.hotel.bookings.BookingEntity;
import com.mjc.hotel.bookings.IBooking;
import com.mjc.hotel.common.dto.BaseEntity;
import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity extends BaseEntity implements IPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Transient
    private Long bookingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private BookingEntity booking;

    @Column(name = "transaction_num", nullable = false, length = 50)
    private String transactionNum;

    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "KRW";

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "card_company")
    private String cardCompany;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "coupon_id")
    @Builder.Default
    private Long couponId = 0L;

    @Column(name = "used_point", nullable = false)
    @Builder.Default
    private BigDecimal usedPoint = BigDecimal.ZERO;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "pg_transaction_id", length = 100)
    private String pgTransactionId;

    @Column(name = "order_id", length = 100)
    private String orderId;

    @Column(name = "payment_key", length = 100)
    private String paymentKey;

    @Column(name = "provider", length = 50)
    private String provider;

    @Column(name = "receipt_url", length = 300)
    private String receiptUrl;


    @Override
    public Long getBookingId() {
        // Long bookingId 랑 booking.bookingId() 랑 값이 항상 같도록 해야 한다.
        if ( this.booking == null ) {
            this.booking = new BookingEntity();
        }
        if ( this.booking.getBookingId() != null) {
            this.bookingId = this.booking.getBookingId();
        } else {
            this.booking.setRoomId(this.bookingId);
        }
        return this.booking.getBookingId();
    }

    @Override
    public void setBookingId(Long bookingId) {
        // Long bookingId 랑 room.getId() 랑 값이 항상 같도록 해야 한다.
        if ( this.booking == null ) {
            this.booking = new BookingEntity();
        }
        this.booking.setRoomId(bookingId);
        this.bookingId = bookingId;
    }

    @Override
    public void setBooking(IBooking booking) {
        // Long bookingId 랑 booking.getBookingId() 랑 값이 항상 같도록 해야 한다.
        if ( booking == null ) {
            return;
        }
        if ( this.booking == null ) {
            this.booking = new BookingEntity();
        }
        this.booking.copyMembers(booking, true);
        this.bookingId = booking.getBookingId();
    }


    @Column(name = "fail_code", length = 100)
    private String failCode;

    @Column(name = "fail_message", length = 500)
    private String failMessage;
}
