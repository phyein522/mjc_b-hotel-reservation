package com.mjc.hotel.promotionSale;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.promotion.IPromotion;
import com.mjc.hotel.promotion.PromotionDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PromotionSaleDto extends BaseDto implements IPromotionSale {

    private Long proSaleId;
    private String saleDes;

    private Long proId;
    private PromotionDto promotion;
    private Long userId;

    @Override
    public Long getProId() {

        if (promotion != null) {
            return promotion.getProId();
        }

        return proId;
    }

    @Override
    public void setProId(Long proId) {

        this.proId = proId;

        if (promotion == null) {
            promotion = new PromotionDto();
        }

        promotion.setProId(proId);
    }
    @Override
    public void setPromotion(IPromotion promotion) {

        if (promotion == null) {
            return;
        }

        if (this.promotion == null) {
            this.promotion = new PromotionDto();
        }

        this.promotion.copyMembers(promotion, true);
        this.proId = promotion.getProId();
    }
}
