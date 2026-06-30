package com.mjc.hotel.hotels;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotelid")
    private Long hotelId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 30)
    private String zipCode;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false)
    private LocalTime checkIn;

    @Column(nullable = false)
    private LocalTime checkOut;

    @Column
    private Short starRate;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private String latitude;

    @Column(nullable = false)
    private String longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HotelTypeEnum type;
}