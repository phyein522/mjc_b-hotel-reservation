package com.mjc.hotel.coupon;

import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.UserEntity;
import com.mjc.hotel.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class CouponService {
    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    // Entity 목록을 응답용 DTO 목록으로 변환한다.
    private List<CouponDto> getListCouponDto(List<CouponEntity> list) {
        return list.stream()
                .map(x -> (CouponDto) new CouponDto().copyMembers(x, true))
                .toList();
    }
    // 쿠폰 목록을 페이지 단위로 조회한다.
    public Page<CouponDto> findAll(Pageable pageable) {
        Page<CouponEntity> page = this.couponRepository.findAll(pageable);
        List<CouponDto> list = this.getListCouponDto(page.getContent());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public List<CouponDto> findAvailable(BigDecimal orderAmount) {
        return this.couponRepository
                .findAllByStatusAndExpirationDateGreaterThanEqual(CouponStatusEnum.ACTIVE, LocalDate.now())
                .stream()
                .filter(coupon -> isEligible(coupon, orderAmount))
                .map(x -> (CouponDto) new CouponDto().copyMembers(x, true))
                .toList();
    }

    public BigDecimal calculateDiscount(Long couponId, BigDecimal orderAmount) {
        if (couponId == null || couponId <= 0) {
            return BigDecimal.ZERO;
        }
        if (orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("쿠폰 적용 주문금액이 올바르지 않습니다.");
        }

        CouponEntity coupon = this.couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));
        if (!isEligible(coupon, orderAmount)) {
            throw new IllegalArgumentException("현재 주문 조건에 사용할 수 없는 쿠폰입니다.");
        }

        BigDecimal discount = coupon.getDiscountType() == CouponDiscountTypeEnum.RATE
                ? orderAmount.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN)
                : coupon.getDiscountValue();
        if (coupon.getMaxDiscount() != null && coupon.getMaxDiscount().signum() > 0) {
            discount = discount.min(coupon.getMaxDiscount());
        }
        return discount.max(BigDecimal.ZERO).min(orderAmount);
    }

    private boolean isEligible(CouponEntity coupon, BigDecimal orderAmount) {
        if (coupon == null
                || coupon.getStatus() != CouponStatusEnum.ACTIVE
                || coupon.getExpirationDate() == null
                || coupon.getExpirationDate().isBefore(LocalDate.now())) {
            return false;
        }
        return orderAmount == null
                || orderAmount.compareTo(BigDecimal.ZERO) <= 0
                || coupon.getMinOrder() == null
                || coupon.getMinOrder().compareTo(orderAmount) <= 0;
    }

    // 쿠폰 ID로 쿠폰 정보를 조회한다.
    public CouponDto findById(Long couponId) {

        CouponEntity entity = this.couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        return (CouponDto) new CouponDto().copyMembers(entity, true);
    }
    // 쿠폰 정보를 검증하고, 요청 사용자가 관리자 권한인지 확인한 뒤 등록한다.
    public CouponDto insert(ICoupon couponDto) {
        CouponEntity insertEntity =
                (CouponEntity) new CouponEntity().copyMembers(couponDto, true);
        validateCoupon(insertEntity);
        validateCouponManager(insertEntity);
        insertEntity.setCouponId(null);
        CouponEntity insertedEntity = this.couponRepository.save(insertEntity);
        return (CouponDto) new CouponDto().copyMembers(insertedEntity, true);
    }

    // 기존 쿠폰 정보를 검증하고, 요청 사용자가 관리자 권한인지 확인한 뒤 수정한다.
    public CouponDto update(ICoupon couponDto) {
        if (couponDto.getCouponId() == null) {
            throw new IllegalArgumentException("쿠폰 ID가 없습니다.");
        }
        CouponDto findDto = this.findById(couponDto.getCouponId());
        findDto.copyMembers(couponDto, false);
        CouponEntity updateEntity =
                (CouponEntity) new CouponEntity().copyMembers(findDto, true);
        validateCoupon(updateEntity);
        validateCouponManager(updateEntity);
        CouponEntity updatedEntity = this.couponRepository.save(updateEntity);
        return (CouponDto) new CouponDto().copyMembers(updatedEntity, true);
    }

    // 쿠폰 ID로 삭제하고, 삭제 전 정보를 반환한다.
    public CouponDto deleteById(Long couponId) {
        CouponDto findDto = this.findById(couponId);
        this.couponRepository.deleteById(couponId);
        return findDto;
    }

    // 요청 사용자가 관리자 권한일 때만 쿠폰을 삭제한다.
    public CouponDto deleteById(Long couponId, Long userId) {

        CouponDto findDto = this.findById(couponId);
        findDto.setUserId(userId);
        validateCouponManager(findDto);
        this.couponRepository.deleteById(couponId);
        return findDto;
    }

    // 쿠폰 등록/수정/삭제 요청자가 ADMIN 또는 SUPER_ADMIN 권한인지 검증한다.
    private void validateCouponManager(ICoupon coupon) {

        if (coupon.getUserId() == null) {
            throw new IllegalArgumentException("관리자 userId가 필요합니다.");
        }

        UserEntity user = this.userRepository.findById(coupon.getUserId())
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getRole() != Role.ADMIN &&
                user.getRole() != Role.SUPER_ADMIN) {

            throw new IllegalArgumentException("관리자만 쿠폰을 관리할 수 있습니다.");
        }

        coupon.setUser(user);
    }

    // 쿠폰 등록/수정에 필요한 필수값과 할인 값, 만료일을 검증한다.
    private void validateCoupon(ICoupon coupon) {

        if (coupon.getCode() == null || coupon.getCode().isBlank()) {
            throw new IllegalArgumentException("쿠폰 코드를 입력하세요.");
        }

        if (coupon.getName() == null || coupon.getName().isBlank()) {
            throw new IllegalArgumentException("쿠폰 이름을 입력하세요.");
        }

        if (coupon.getDiscountType() == null) {
            throw new IllegalArgumentException("할인 종류를 선택하세요.");
        }

        if (coupon.getDiscountValue() == null) {
            throw new IllegalArgumentException("할인 값을 입력하세요.");
        }

        if (coupon.getDiscountValue().signum() <= 0) {
            throw new IllegalArgumentException("할인 값은 0보다 커야 합니다.");
        }

        if (coupon.getExpirationDate() == null) {
            throw new IllegalArgumentException("만료일을 입력하세요.");
        }

        if (coupon.getExpirationDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("만료일은 오늘 이후여야 합니다.");
        }

        if (coupon.getStatus() == null) {
            throw new IllegalArgumentException("쿠폰 상태를 입력하세요.");

        }
    }
}
