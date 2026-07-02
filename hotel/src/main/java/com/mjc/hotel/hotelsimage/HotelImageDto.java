package com.mjc.hotel.hotelsimage;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.hotels.HotelDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class HotelImageDto extends BaseDto implements IHotelImage {
    private Long imageId;
    private String url;
    private Integer sortOrder;
    private Boolean isThumbnail;

    private Long hotelId;
    private HotelDto hotel;

    @Override
    public Long getHotelId() {

        if(hotel != null){
            return hotel.getHotelId();
        }

        return hotelId;
    }

    @Override
    public void setHotelId(Long hotelId) {

        this.hotelId = hotelId;

        if (hotel == null) {
            hotel = new HotelDto();
        }
        hotel.setHotelId(hotelId);
    }
}