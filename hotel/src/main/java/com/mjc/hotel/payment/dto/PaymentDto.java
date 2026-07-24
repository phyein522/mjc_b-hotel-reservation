package com.mjc.hotel.payment.dto;

import com.mjc.hotel.bookings.BookingDto;
import com.mjc.hotel.bookings.IBooking;
import com.mjc.hotel.common.dto.BaseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
public class PaymentDto extends BaseDto implements IPayment {
    private Long paymentId;
    private Long bookingId;
    private BookingDto booking;
    private String transactionNum;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private BigDecimal totalAmount;
    private String currency;
    private LocalDateTime approvedAt;
    private LocalDateTime cancelledAt;
    private String cardCompany;
    private String cardNumber;
    private Long couponId;
    private BigDecimal usedPoint;
    private BigDecimal discountAmount;
    private String pgTransactionId;
    private String orderId;
    private String paymentKey;
    private String provider;
    private String receiptUrl;
    private String failCode;
    private String failMessage;


    @Override
    public Long getBookingId() {
        // Long bookingId 랑 booking.bookingId() 랑 값이 항상 같도록 해야 한다.
        if ( this.booking == null ) {
            this.booking = new BookingDto();
        }
        if ( this.booking.getBookingId() != null) {
            this.bookingId = this.booking.getBookingId();
        } else {
            this.booking.setBookingId(this.bookingId);
        }
        return this.booking.getBookingId();
    }

    @Override
    public void setBookingId(Long bookingId) {
        // Long bookingId 랑 booking.getId() 랑 값이 항상 같도록 해야 한다.
        if ( this.booking == null ) {
            this.booking = new BookingDto();
        }
        this.booking.setBookingId(bookingId);
        this.bookingId = bookingId;
    }

    @Override
    public void setBooking(IBooking booking) {
        // Long bookingId 랑 booking.getBookingId() 랑 값이 항상 같도록 해야 한다.
        if ( booking == null ) {
            return;
        }
        if ( this.booking == null ) {
            this.booking = new BookingDto();
        }
        this.booking.copyMembers(booking, true);
        this.bookingId = booking.getBookingId();
    }
}
