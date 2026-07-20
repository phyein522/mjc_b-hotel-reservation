package com.mjc.hotel.promotionSale;

import com.mjc.hotel.promotion.PromotionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionSaleRepository extends JpaRepository<PromotionSaleEntity, Long> {
    Page<PromotionSaleEntity> findAllByPromotionEquals(PromotionEntity promotion, Pageable pageable);
}
