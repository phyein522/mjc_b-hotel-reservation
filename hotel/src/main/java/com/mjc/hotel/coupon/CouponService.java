package com.mjc.hotel.coupon;

import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.UserEntity;
import com.mjc.hotel.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    // 쿠폰 ID로 쿠폰 정보를 조회한다.
    public CouponDto findById(Long couponId) {

        CouponEntity entity = this.couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        return (CouponDto) new CouponDto().copyMembers(entity, true);
    }
    // 호텔 기본 정보를 등록하고, 등록 사용자가 호텔 매니저인지 확인한다.
    public CouponDto insert(ICoupon couponDto) {
        CouponEntity insertEntity =
                (CouponEntity) new CouponEntity().copyMembers(couponDto, true);
        insertEntity.setCouponId(null);
        validateCoupon(insertEntity);
        validateCouponManager(insertEntity);
        CouponEntity insertedEntity = this.couponRepository.save(insertEntity);
        return (CouponDto) new CouponDto().copyMembers(insertedEntity, true);
    }
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
    public CouponDto deleteById(Long couponId, Long userId) {

        CouponEntity entity = this.couponRepository.findById(couponId)
                .orElseThrow(() ->
                        new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
        entity.setUserId(userId);
        validateCouponManager(entity);
        this.couponRepository.delete(entity);
        return (CouponDto) new CouponDto().copyMembers(entity, true);
    }
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
