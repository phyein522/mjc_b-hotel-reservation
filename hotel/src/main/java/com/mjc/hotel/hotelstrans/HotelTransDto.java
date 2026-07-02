package com.mjc.hotel.hotelstrans;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.hotels.HotelDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelTransDto extends BaseDto implements IHotelTrans{
    private Long transId;
    private String name;
    private String time;
    private String depart;

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
}
