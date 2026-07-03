package com.mjc.hotel.sales_analysis.entity;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FakeRoom {
    @Id
    @Column(name = "room_id")
    private Long id;

    @Column(name = "room_number", nullable = false, length = 50)
    private String roomNumber;

    private Integer floor;

    @Column(nullable = false)
    private String status;

    @Column(length = 100)
    private String name;

    private Integer size;

    @Column(name = "base_price", precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "max_adult")
    private Integer maxAdult;

    @Column(name = "max_children")
    private Integer maxChildren;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "호텔아이디", nullable = false)
    private FakeHotel hotel;

    @Column(name = "room_type_id")
    private String roomTypeId;

    @Column(name = "room_kind_id")
    private String roomKindId;
}
