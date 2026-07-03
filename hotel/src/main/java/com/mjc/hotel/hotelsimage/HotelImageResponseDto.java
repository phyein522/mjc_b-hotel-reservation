package com.mjc.hotel.hotelsimage;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
public class HotelImageResponseDto extends HotelImageDto {
    private String imageUrl;

    public String getImageUrl() {

        if(imageUrl == null){

            imageUrl = String.format(
                    "http://localhost:8989/api/hotelimage/image/%d",
                    this.getHotelImageId());
        }

        return imageUrl;
    }
}
