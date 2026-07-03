package com.mjc.hotel.sales_analysis.entity;

import com.mjc.hotel.user.entity.User;
import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.rooms.dto.RoomEntity;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "payment")
public class FakeBooking {
    @Id
    @Column(name = "booking_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    @Column(name = "channel_id")
    private Long channelId;

    @Column(name = "booking_no", nullable = false, length = 50)
    private String bookingNo;

    @Column(name = "guest_last_name", length = 50)
    private String guestLastName;

    @Column(name = "guest_first_name", length = 50)
    private String guestFirstName;

    @Column(name = "guest_phone", length = 30)
    private String guestPhone;

    @Column(name = "guest_email", length = 255)
    private String guestEmail;

    @Column(length = 10)
    private String nationality;

    private Integer nights;

    @Column(name = "guest_count")
    private Integer guestCount;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FakePayment payment;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
}
