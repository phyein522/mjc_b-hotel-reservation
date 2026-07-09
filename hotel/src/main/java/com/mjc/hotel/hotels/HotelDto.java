package com.mjc.hotel.hotels;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.common.dto.IBase;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class HotelDto extends BaseDto implements IHotel{
    private Long hotelId;
    private String name;
    private String description;
    private String address;
    private String city;
    private String zipCode;
    private String phone;
    private String email;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private int starRate;
    private Boolean isActive;
    private String latitude;
    private String longitude;
    private HotelTypeEnum type;
}
