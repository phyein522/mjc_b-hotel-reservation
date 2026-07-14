package com.mjc.hotel.hotels;

import com.mjc.hotel.hotelsimage.HotelImageResponseDto;
import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponseWithImagesDto {
    private HotelDto hotelDto;
    private Page<HotelImageResponseDto> hotelImages;
}
