package com.mjc.hotel.hotelstrans;

import com.mjc.hotel.hotels.HotelDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelTransDto {
    private Long transId;
    private String name;
    private int time;
    private String depart;

    private Long hotelId;
    private HotelDto hotel;
}
