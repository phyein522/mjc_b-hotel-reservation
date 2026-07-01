package com.mjc.hotel.hotelsattr;

import com.mjc.hotel.hotels.HotelDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelAttrDto {
    private Long attrId;
    private String context;

    private Long hotelId;
    private HotelDto hotel;
}
