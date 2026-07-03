package com.mjc.hotel.sales_analysis.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FakeHotel {
    @Id
    @Column(name = "hotel_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    private String zipCode;
    private String phone;
    private String email;

    @Column(name = "checkIn", nullable = false)
    private LocalTime checkInTime;

    @Column(name = "checkOut", nullable = false)
    private LocalTime checkOutTime;

    private Integer starRate;

    @Column(name = "isActive", nullable = false)
    private Boolean isActive;

    private String latitude;
    private String longitude;
    private Integer type;
}
