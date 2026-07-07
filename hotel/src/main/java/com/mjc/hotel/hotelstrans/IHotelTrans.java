package com.mjc.hotel.hotelstrans;

import com.mjc.hotel.common.dto.IBase;
import com.mjc.hotel.promotionSale.PromotionSaleDto;

@tools.jackson.databind.annotation.JsonDeserialize(as = PromotionSaleDto.class)
public interface IHotelTrans extends IBase {

    Long getTransId();
    void setTransId(Long transId);

    String getName();
    void setName(String name);

    String getTime();
    void setTime(String time);

    String getDepart();
    void setDepart(String depart);

    Long getHotelId();
    void setHotelId(Long hotelId);

    default IHotelTrans copyMembers(IHotelTrans source, boolean forced) {

        if (source == null) {
            return this;
        }

        IBase.super.copyMembers(source, forced);

        if (forced || source.getTransId() != null) {
            this.setTransId(source.getTransId());
        }

        if (forced || source.getName() != null) {
            this.setName(source.getName());
        }

        if (forced || source.getTime() != null) {
            this.setTime(source.getTime());
        }

        if (forced || source.getDepart() != null) {
            this.setDepart(source.getDepart());
        }

        if (forced || source.getHotelId() != null) {
            this.setHotelId(source.getHotelId());
        }

        return this;
    }
}