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
    private String transName;
    private int transTime;
    private String transDepart;

    private Long hotelId;
    private HotelDto hotel;
}
