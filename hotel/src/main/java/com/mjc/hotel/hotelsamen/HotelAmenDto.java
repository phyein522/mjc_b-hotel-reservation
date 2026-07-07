package com.mjc.hotel.hotelsamen;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.hotels.HotelDto;
import com.mjc.hotel.hotels.IHotel;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelAmenDto extends BaseDto implements IHotelAmen {
    private Long amenId;
    private Boolean wifi;
    private Boolean pool;
    private Boolean gym;
    private Boolean spa;
    private Boolean restaurant;
    private Boolean valetParking;
    private Boolean bar;

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
