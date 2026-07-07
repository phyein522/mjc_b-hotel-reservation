package com.mjc.hotel.promotionSale;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.promotion.PromotionEntity;
import com.mjc.hotel.promotion.IPromotion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "promotion_sale")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionSaleEntity extends BaseEntity implements IPromotionSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proSaleId;

    @Column(nullable = false)
    private String saleDes;

    @Transient
    private Long proId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pro_id", nullable = false)
    private PromotionEntity promotion;

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

        if (this.promotion == null) {
            this.promotion = new PromotionEntity();
        }

        this.promotion.setProId(proId);
    }
    @Override
    public void setPromotion(IPromotion promotion) {

        if (promotion == null) {
            return;
        }

        if (this.promotion == null) {
            this.promotion = new PromotionEntity();
        }

        this.promotion.copyMembers(promotion, true);
        this.proId = promotion.getProId();
    }
}