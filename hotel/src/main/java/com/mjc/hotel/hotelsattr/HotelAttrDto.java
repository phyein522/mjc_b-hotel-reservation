package com.mjc.hotel.hotelsattr;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.hotels.HotelDto;
import com.mjc.hotel.hotels.IHotel;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelAttrDto extends BaseDto implements IHotelAttr {
    private Long attrId;
    private String context;

    private Long hotelId;
    private HotelDto hotel;

    @Override
    public Long getHotelId() {

        if (hotel != null) {
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
    @Override
    public void setHotel(IHotel hotel) {

        if (hotel == null) {
            return;
        }

        if (this.hotel == null) {
            this.hotel = new HotelDto();
        }

        this.hotel.copyMembers(hotel, true);
        this.hotelId = hotel.getHotelId();
    }
}
