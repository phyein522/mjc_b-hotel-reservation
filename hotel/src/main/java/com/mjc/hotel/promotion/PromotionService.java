package com.mjc.hotel.promotion;

import com.mjc.hotel.rooms.dto.RoomEntity;
import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.UserEntity;
import com.mjc.hotel.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PromotionService {
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private UserRepository userRepository;

    // Entity 목록을 응답용 DTO 목록으로 변환한다.
    private List<PromotionDto> getListPromotionDto(List<PromotionEntity> list) {
        return list.stream()
                .map(x -> (PromotionDto) new PromotionDto().copyMembers(x, true))
                .toList();
    }

    // 프로모션 목록을 페이지 단위로 조회한다.
    public Page<PromotionDto> findAll(Pageable pageable) {
        Page<PromotionEntity> page = this.promotionRepository.findAll(pageable);
        List<PromotionDto> list = this.getListPromotionDto(page.getContent());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    // 프로모션 ID로 프로모션 정보를 조회한다.
    public PromotionDto findById(Long proId) {

        PromotionEntity entity = this.promotionRepository.findById(proId)
                .orElseThrow(() -> new IllegalArgumentException("프로모션을 찾을 수 없습니다."));

        return (PromotionDto) new PromotionDto().copyMembers(entity, true);
    }

    // 프로모션 정보를 검증한 뒤 등록한다.
    public PromotionDto insert(IPromotion requestDto) {

        PromotionEntity entity =
                (PromotionEntity) new PromotionEntity().copyMembers(requestDto, true);

        this.validatePromotion(entity);
        this.validatePromotionManager(entity.getUserId(), entity.getRoomId());
        entity.setProId(null);

        PromotionEntity inserted =
                promotionRepository.save(entity);

        return (PromotionDto) new PromotionDto().copyMembers(inserted, true);
    }

    // 기존 프로모션 정보를 검증한 뒤 수정한다.
    public PromotionDto update(IPromotion requestDto) {

        PromotionDto findDto = this.findById(requestDto.getProId());

        findDto.copyMembers(requestDto, false);

        PromotionEntity updateEntity =
                (PromotionEntity) new PromotionEntity().copyMembers(findDto, true);

        this.validatePromotion(updateEntity);
        this.validatePromotionManager(requestDto.getUserId(), updateEntity.getRoomId());
        PromotionEntity updated =
                promotionRepository.save(updateEntity);

        return (PromotionDto) new PromotionDto().copyMembers(updated, true);
    }

    // 프로모션 ID로 삭제하고, 삭제 전 정보를 반환한다.
    public PromotionDto deleteById(Long proId) {
        PromotionDto findDto = this.findById(proId);
        this.promotionRepository.deleteById(proId);
        return findDto;
    }

    // 요청 사용자가 관리 권한을 가진 프로모션만 삭제한다.
    public PromotionDto deleteById(Long proId, Long userId) {
        PromotionDto findDto = this.findById(proId);
        this.validatePromotionManager(userId, findDto.getRoomId());
        this.promotionRepository.deleteById(proId);
        return findDto;
    }

    // 객실 ID에 속한 프로모션 목록을 페이지 단위로 조회한다.
    public Page<PromotionDto> findAllByRoomIdEquals(Long roomId, Pageable pageable) {
        RoomEntity room = RoomEntity.builder().roomId(roomId).build();
        Page<PromotionEntity> page = this.promotionRepository.findAllByRoomEquals(room, pageable);
        List<PromotionDto> list = this.getListPromotionDto(page.getContent());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    // 프로모션 필수값과 같은 객실의 기간 중복 여부를 검증한다.
    private void validatePromotion(PromotionEntity promotion) {
        if (promotion.getRoomId() == null) {
            throw new IllegalArgumentException("프로모션을 적용할 roomId가 필요합니다.");
        }
        if (promotion.getStartDate() == null || promotion.getEndDate() == null) {
            throw new IllegalArgumentException("프로모션 시작일과 종료일이 필요합니다.");
        }
        if (!promotion.getStartDate().isBefore(promotion.getEndDate())) {
            throw new IllegalArgumentException("프로모션 종료일은 시작일보다 이후여야 합니다.");
        }
        if (promotion.getDisType() == null) {
            throw new IllegalArgumentException("프로모션 할인 유형이 필요합니다.");
        }
        this.parseDiscountValue(promotion.getDisValue());

        RoomEntity room = RoomEntity.builder().roomId(promotion.getRoomId()).build();
        List<PromotionEntity> overlaps = this.promotionRepository.findOverlappingPromotions(
                room,
                promotion.getProId(),
                promotion.getStartDate(),
                promotion.getEndDate()
        );
        if (!overlaps.isEmpty()) {
            throw new IllegalArgumentException("같은 객실에 기간이 겹치는 프로모션이 있습니다.");
        }
    }

    // 프로모션 ID를 기준으로 해당 프로모션의 객실 관리 권한을 검증한다.
    public void validatePromotionManagerByPromotionId(Long userId, Long proId) {
        PromotionDto promotion = this.findById(proId);
        this.validatePromotionManager(userId, promotion.getRoomId());
    }

    // 관리자 또는 해당 객실의 호텔 매니저만 프로모션을 관리할 수 있게 검증한다.
    public void validatePromotionManager(Long userId, Long roomId) {
        if (userId == null) {
            throw new IllegalArgumentException("프로모션을 관리할 userId가 필요합니다.");
        }
        if (roomId == null) {
            throw new IllegalArgumentException("프로모션을 적용할 roomId가 필요합니다.");
        }

        UserEntity user = this.userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPER_ADMIN) {
            return;
        }

        if (user.getRole() != Role.HOTEL_MANAGER) {
            throw new IllegalArgumentException("호텔 관리자만 프로모션을 관리할 수 있습니다.");
        }

        Long hotelManagerUserId = this.promotionRepository.findHotelManagerUserIdByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("객실의 호텔 관리자를 찾을 수 없습니다."));

        if (!hotelManagerUserId.equals(userId)) {
            throw new IllegalArgumentException("자신이 관리하는 호텔 객실의 프로모션만 관리할 수 있습니다.");
        }
    }

    // 할인 값이 비어 있지 않고 0 이상의 숫자인지 확인한다.
    private BigDecimal parseDiscountValue(BigDecimal disValue) {
        if (disValue == null) {
            throw new IllegalArgumentException("프로모션 할인 값이 필요합니다.");
        }

        if (disValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("프로모션 할인 값은 0 이상이어야 합니다.");
        }

        return disValue;
    }
}
