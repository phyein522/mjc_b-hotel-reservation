package com.mjc.hotel.hotelsimage;

import com.mjc.hotel.hotels.HotelDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class HotelImageDto {
    private Long imageId;
    private String url;
    private Integer sortOrder;
    private Boolean isThumbnail;

    private Long hotelId;
    private HotelDto hotel;

}