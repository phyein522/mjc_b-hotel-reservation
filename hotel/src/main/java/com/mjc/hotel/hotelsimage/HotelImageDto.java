package com.mjc.hotel.hotelsimage;

import com.mjc.hotel.hotels.HotelDto;
import com.mjc.hotel.hotels.HotelMapper;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table
@Builder
public class HotelImageDto {
    private Long id;
    private String url;
    private Integer sortOrder;
    private Boolean isThumbnail;

    private Long Id;
    private HotelDto hotel;

}