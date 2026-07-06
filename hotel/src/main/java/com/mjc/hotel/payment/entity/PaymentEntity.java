package com.mjc.hotel.payment.entity;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.payment.dto.IPayment;
import com.mjc.hotel.payment.enums.PaymentMethod;
import com.mjc.hotel.payment.enums.PaymentStatus;
import com.mjc.hotel.rooms.dto.RoomEntity;
import com.mjc.hotel.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", comment = "결제 정보")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PaymentEntity extends BaseEntity implements IPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", comment = "기본키")
    private Long paymentId;

    @Transient
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, comment = "사용자 외래키")
    private User user;

    @Transient
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false, comment = "객실 외래키")
    private RoomEntity room;

    @Column(nullable = false, precision = 12, scale = 2, comment = "결제 금액")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20, comment = "결제 수단")
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20, comment = "결제 상태")
    private PaymentStatus paymentStatus;

    @Column(name = "paid_at", comment = "결제 완료 시각")
    private LocalDateTime paidAt;

    @Column(name = "cancelled_at", comment = "취소 시각")
    private LocalDateTime cancelledAt;

    @Column(columnDefinition = "TEXT", comment = "메모")
    private String memo;

    // ===== userId / roomId 동기화 (RoomEntity 패턴 그대로) =====

    @Override
    public Long getUserId() {
        if (this.user == null) this.user = new User();
        if (this.user.getUserId() != null) this.userId = this.user.getUserId();
        else this.user.setUserId(this.userId);
        return this.user.getUserId();
    }

    @Override
    public void setUserId(Long userId) {
        if (this.user == null) this.user = new User();
        this.user.setUserId(userId);
        this.userId = userId;
    }

    @Override
    public Long getRoomId() {
        if (this.room == null) this.room = new RoomEntity();
        if (this.room.getRoomId() != null) this.roomId = this.room.getRoomId();
        else this.room.setRoomId(this.roomId);
        return this.room.getRoomId();
    }

    @Override
    public void setRoomId(Long roomId) {
        if (this.room == null) this.room = new RoomEntity();
        this.room.setRoomId(roomId);
        this.roomId = roomId;
    }
}