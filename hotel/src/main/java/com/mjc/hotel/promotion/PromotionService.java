package com.mjc.hotel.promotion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionService {
    @Autowired
    private PromotionRepository promotionRepository;
    public List<PromotionDto> findAll() {

        return promotionRepository.findAll().stream()
                .map(x -> (PromotionDto) new PromotionDto().copyMembers(x, true))
                .toList();
    }

    public PromotionDto findById(Long proId) {

        PromotionEntity entity =
                promotionRepository.findById(proId).orElseThrow();

        return (PromotionDto) new PromotionDto().copyMembers(entity, true);
    }

    public PromotionDto insert(IPromotion requestDto) {

        PromotionEntity entity =
                (PromotionEntity) new PromotionEntity().copyMembers(requestDto, true);

        entity.setProId(null);

        PromotionEntity inserted =
                promotionRepository.save(entity);

        return (PromotionDto) new PromotionDto().copyMembers(inserted, true);
    }

    public PromotionDto update(Long proId, IPromotion requestDto) {

        PromotionDto findDto = this.findById(proId);

        findDto.copyMembers(requestDto, false);

        PromotionEntity updateEntity =
                (PromotionEntity) new PromotionEntity().copyMembers(findDto, true);

        PromotionEntity updated =
                promotionRepository.save(updateEntity);

        return (PromotionDto) new PromotionDto().copyMembers(updated, true);
    }

    public void deleteById(Long proId) {

        promotionRepository.deleteById(proId);
    }
}
