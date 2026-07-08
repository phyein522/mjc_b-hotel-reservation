package com.mjc.hotel.promotionSale;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionSaleService {

    private final PromotionSaleRepository promotionSaleRepository;

    public List<PromotionSaleDto> findAll() {

        return promotionSaleRepository.findAll().stream()
                .map(x -> (PromotionSaleDto) new PromotionSaleDto().copyMembers(x, true))
                .toList();
    }

    public PromotionSaleDto findById(Long proSaleId) {

        PromotionSaleEntity entity =
                promotionSaleRepository.findById(proSaleId).orElseThrow();

        return (PromotionSaleDto) new PromotionSaleDto().copyMembers(entity, true);
    }

    public PromotionSaleDto insert(IPromotionSale requestDto) {

        PromotionSaleEntity entity =
                (PromotionSaleEntity) new PromotionSaleEntity().copyMembers(requestDto, true);

        entity.setProSaleId(null);

        PromotionSaleEntity inserted =
                promotionSaleRepository.save(entity);

        return (PromotionSaleDto) new PromotionSaleDto().copyMembers(inserted, true);
    }

    public PromotionSaleDto update(Long proSaleId, IPromotionSale requestDto) {

        PromotionSaleDto findDto = this.findById(proSaleId);

        findDto.copyMembers(requestDto, false);

        PromotionSaleEntity updateEntity =
                (PromotionSaleEntity) new PromotionSaleEntity().copyMembers(findDto, true);

        PromotionSaleEntity updated =
                promotionSaleRepository.save(updateEntity);

        return (PromotionSaleDto) new PromotionSaleDto().copyMembers(updated, true);
    }

    public void deleteById(Long proSaleId) {

        promotionSaleRepository.deleteById(proSaleId);
    }
}