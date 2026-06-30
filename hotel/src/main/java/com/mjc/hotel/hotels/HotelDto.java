package com.mjc.hotel.hotels;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class HotelDto {
    private Long hotelId;
    private String hotelName;
    private String hotelDescription;
    private String hotelAddress;
    private String hotelCity;
    private String hotelZipCode;
    private String hotelPhone;
    private String hotelEmail;
    private LocalTime hotelCheckInTime;
    private LocalTime hotelCheckOutTime;
    private Short hotelStarRating;
    private Boolean hotelIsActive;
    private String latitude;
    private String hardness;
    private String hotelType;
}
