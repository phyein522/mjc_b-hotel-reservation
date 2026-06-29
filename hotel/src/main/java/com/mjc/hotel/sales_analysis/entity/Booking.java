package com.mjc.hotel.sales_analysis.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    // 어느 호텔의 예약인지 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "room_type_id", nullable = false)
    private Long roomTypeId;

    @Column(name = "channel_id", nullable = false)
    private Long channelId;

    @Column(name = "booking_no", nullable = false, length = 50)
    private String bookingNo;

    @Column(name = "guest_last_name", nullable = false, length = 100)
    private String guestLastName;

    @Column(name = "guest_first_name", nullable = false, length = 100)
    private String guestFirstName;

    @Column(name = "guest_phone", nullable = false, length = 30)
    private String guestPhone;

    @Column(name = "guest_email", nullable = false, length = 255)
    private String guestEmail;

    @Column(name = "nationality", nullable = false, length = 30)
    private String nationality;

    @Column(name = "special_request", length = 255)
    private String specialRequest;

    @Column(name = "nights")
    private Integer nights;

    @Column(name = "guest_count", nullable = false)
    private Integer guestCount;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_in_time", nullable = false)
    private LocalTime checkInTime;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "check_out_time", nullable = false)
    private LocalTime checkOutTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 취소된 예약은 매출에서 제외
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "point")
    private Integer point;
}