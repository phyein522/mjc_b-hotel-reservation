package com.mjc.hotel.promotionSale;

import com.mjc.hotel.common.dto.IBase;
import com.mjc.hotel.promotion.IPromotion;
import org.hibernate.LazyInitializationException;

@tools.jackson.databind.annotation.JsonDeserialize(as = PromotionSaleDto.class)
public interface IPromotionSale extends IBase {
    Long getProSaleId();
    void setProSaleId(Long proSaleId);

    String getSaleDes();
    void setSaleDes(String saleDes);

    Long getProId();
    void setProId(Long proId);

    IPromotion getPromotion();
    void setPromotion(IPromotion promotion);

    default IPromotionSale copyMembers(IPromotionSale source, boolean forced) {

        if (source == null) {
            return this;
        }

        IBase.super.copyMembers(source, forced);

        if (forced || source.getProSaleId() != null) {
            this.setProSaleId(source.getProSaleId());
        }
        if (forced || source.getSaleDes() != null) {
            this.setSaleDes(source.getSaleDes());
        }
        if (forced || source.getProId() != null) {
            this.setProId(source.getProId());

            try {
                this.getPromotion().copyMembers(source.getPromotion(), forced);
            } catch (LazyInitializationException e) {
                System.err.println(e.getMessage());
            }
        }
        return this;
    }
}
