package com.mjc.hotel.hotelsimage;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.hotels.HotelDto;
import com.mjc.hotel.hotels.IHotel;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class HotelImageDto extends BaseDto implements IHotelImage {
    private Long hotelImageId;
    private String fileName;
    private Integer size;
    private String ext;
    private String storeName;
    private String path;

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
        this.hotelId = hotelId;
    }
    public void setHotel(IHotel hotel) {

        if(hotel == null){
            return;
        }
        if(this.hotel== null){
            this.hotel = new HotelDto();
        }
        this.hotel.copyMembers(hotel, true);
        this.hotelId = hotel.getHotelId();
    }
    @Override
    public HotelDto getHotel() {
        return hotel;
    }
}
