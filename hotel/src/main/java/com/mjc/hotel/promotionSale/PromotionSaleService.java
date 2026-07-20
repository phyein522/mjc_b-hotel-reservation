package com.mjc.hotel.promotionSale;

import com.mjc.hotel.promotion.PromotionEntity;
import com.mjc.hotel.promotion.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionSaleService {

    @Autowired
    private PromotionSaleRepository promotionSaleRepository;
    @Autowired
    private PromotionService promotionService;

    // Entity 목록을 응답용 DTO 목록으로 변환한다.
    private List<PromotionSaleDto> getListPromotionSaleDto(List<PromotionSaleEntity> list) {
        return list.stream()
                .map(x -> (PromotionSaleDto) new PromotionSaleDto().copyMembers(x, true))
                .toList();
    }

    // 프로모션 할인 내용 목록을 페이지 단위로 조회한다.
    public Page<PromotionSaleDto> findAll(Pageable pageable) {
        Page<PromotionSaleEntity> page = this.promotionSaleRepository.findAll(pageable);
        List<PromotionSaleDto> list = this.getListPromotionSaleDto(page.getContent());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    // 프로모션 할인 내용 ID로 상세 정보를 조회한다.
    public PromotionSaleDto findById(Long proSaleId) {

        PromotionSaleEntity entity = this.promotionSaleRepository.findById(proSaleId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션 할인 내용을 찾을 수 없습니다."));

        return (PromotionSaleDto) new PromotionSaleDto().copyMembers(entity, true);
    }

    // 프로모션 관리 권한을 확인한 뒤 할인 내용을 등록한다.
    public PromotionSaleDto insert(IPromotionSale requestDto) {

        PromotionSaleEntity entity =
                (PromotionSaleEntity) new PromotionSaleEntity().copyMembers(requestDto, true);

        this.promotionService.validatePromotionManagerByPromotionId(entity.getUserId(), entity.getProId());
        entity.setProSaleId(null);

        PromotionSaleEntity inserted =
                promotionSaleRepository.save(entity);

        return (PromotionSaleDto) new PromotionSaleDto().copyMembers(inserted, true);
    }

    // 프로모션 관리 권한을 확인한 뒤 할인 내용을 수정한다.
    public PromotionSaleDto update(IPromotionSale requestDto) {

        PromotionSaleDto findDto = this.findById(requestDto.getProSaleId());

        findDto.copyMembers(requestDto, false);

        PromotionSaleEntity updateEntity =
                (PromotionSaleEntity) new PromotionSaleEntity().copyMembers(findDto, true);

        this.promotionService.validatePromotionManagerByPromotionId(requestDto.getUserId(), updateEntity.getProId());
        PromotionSaleEntity updated =
                promotionSaleRepository.save(updateEntity);

        return (PromotionSaleDto) new PromotionSaleDto().copyMembers(updated, true);
    }

    // 프로모션 할인 내용 ID로 삭제하고, 삭제 전 정보를 반환한다.
    public PromotionSaleDto deleteById(Long proSaleId) {
        PromotionSaleDto findDto = this.findById(proSaleId);
        this.promotionSaleRepository.deleteById(proSaleId);
        return findDto;
    }

    // 요청 사용자가 관리 권한을 가진 프로모션 할인 내용만 삭제한다.
    public PromotionSaleDto deleteById(Long proSaleId, Long userId) {
        PromotionSaleDto findDto = this.findById(proSaleId);
        this.promotionService.validatePromotionManagerByPromotionId(userId, findDto.getProId());
        this.promotionSaleRepository.deleteById(proSaleId);
        return findDto;
    }

    // 프로모션 ID에 속한 할인 내용 목록을 페이지 단위로 조회한다.
    public Page<PromotionSaleDto> findAllByProIdEquals(Long proId, Pageable pageable) {
        PromotionEntity promotion = PromotionEntity.builder().proId(proId).build();
        Page<PromotionSaleEntity> page = this.promotionSaleRepository.findAllByPromotionEquals(promotion, pageable);
        List<PromotionSaleDto> list = this.getListPromotionSaleDto(page.getContent());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }
}
