package com.mjc.hotel.hotelsattr;

import com.mjc.hotel.common.dto.IBase;
import com.mjc.hotel.hotels.IHotel;
import org.hibernate.LazyInitializationException;

@tools.jackson.databind.annotation.JsonDeserialize(as = HotelAttrDto.class)
public interface IHotelAttr extends IBase {

    Long getAttrId();
    void setAttrId(Long attrId);

    String getContext();
    void setContext(String context);

    Long getHotelId();
    void setHotelId(Long hotelId);

    IHotel getHotel();
    void setHotel(IHotel hotel);

    default IHotelAttr copyMembers(IHotelAttr source, boolean forced) {

        if (source == null) {
            return this;
        }

        IBase.super.copyMembers(source, forced);

        if (forced || source.getAttrId() != null) {
            this.setAttrId(source.getAttrId());
        }

        if (forced || source.getContext() != null) {
            this.setContext(source.getContext());
        }

        if (forced || source.getHotelId() != null) {
            this.setHotelId(source.getHotelId());

            try {
                if (source.getHotel() != null) {
                    this.setHotel(source.getHotel());
                }
            } catch (LazyInitializationException e) {
                System.err.println(e.getMessage());
            }
        }

        return this;
    }
}