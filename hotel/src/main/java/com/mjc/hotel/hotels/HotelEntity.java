package com.mjc.hotel.hotels;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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

    @Column(name = "hotel_name", nullable = false, length = 200)
    private String hotelName;

    @Column(name = "hotel_description", columnDefinition = "TEXT")
    private String hotelDescription;

    @Column(name = "hotel_address", nullable = false, length = 500)
    private String hotelAddress;

    @Column(name = "hotel_city", nullable = false, length = 100)
    private String hotelCity;

    @Column(name = "hotel_zip_code", nullable = false, length = 30)
    private String hotelZipCode;

    @Column(name = "hotel_phone", nullable = false, length = 30)
    private String hotelPhone;

    @Column(name = "hotel_email", nullable = false, length = 255)
    private String hotelEmail;

    @Column(name = "hotel_checkintime", nullable = false)
    private LocalTime hotelCheckInTime;

    @Column(name = "hotel_checkouttime", nullable = false)
    private LocalTime hotelCheckOutTime;

    @Column(name = "hotel_starrating")
    private Short hotelStarRating;

    @Column(name = "hotel_isactive", nullable = false)
    private Boolean hotelIsActive;

    @Column(nullable = false)
    private String latitude;

    @Column(nullable = false)
    private String hardness;

    @Column(nullable = false)
    private String hotelType;
}