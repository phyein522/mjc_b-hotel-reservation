package com.mjc.hotel.usercoupon;

import com.mjc.hotel.coupon.CouponRepository;
import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.UserEntity;
import com.mjc.hotel.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserCouponService {
    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    // Entity 목록을 응답용 DTO 목록으로 변환한다.
    private List<UserCouponDto> getListCouponDto(List<UserCouponEntity> list) {
        return list.stream()
                .map(x -> (UserCouponDto) new UserCouponDto().copyMembers(x, true))
                .toList();
    }
    // 쿠폰 목록을 페이지 단위로 조회한다.
    public Page<UserCouponDto> findAll(Pageable pageable) {
        Page<UserCouponEntity> page = this.userCouponRepository.findAll(pageable);
        List<UserCouponDto> list = this.getListCouponDto(page.getContent());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }
    // 쿠폰 ID로 쿠폰 정보를 조회한다.
    public UserCouponDto findById(Long userCouponId) {

        UserCouponEntity entity = this.userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        return (UserCouponDto) new UserCouponDto().copyMembers(entity, true);
    }
    // 호텔 기본 정보를 등록하고, 등록 사용자가 호텔 매니저인지 확인한다.
    public UserCouponDto insert(IUserCoupon userCouponDto) {

        UserCouponEntity insertEntity =
                (UserCouponEntity) new UserCouponEntity().copyMembers(userCouponDto, true);
        insertEntity.setUserCouponId(null);
        insertEntity.setIssuedAt(LocalDateTime.now());
        insertEntity.setUserCouponStatus(UserCouponStatusEnum.AVAILABLE);
        validateCoupon(insertEntity);
        if (userCouponRepository.countCoupon(
                insertEntity.getUserId(),
                insertEntity.getCouponId()) > 0) {

            throw new IllegalArgumentException("이미 발급된 쿠폰입니다.");
        }
        validateCouponManager(insertEntity);
        UserCouponEntity insertedEntity = this.userCouponRepository.save(insertEntity);
        return (UserCouponDto) new UserCouponDto().copyMembers(insertedEntity, true);
    }

    public UserCouponDto update(IUserCoupon userCouponDto) {
        if (userCouponDto.getUserCouponId() == null) {
            throw new IllegalArgumentException("쿠폰 ID가 없습니다.");
        }
        UserCouponDto findDto = this.findById(userCouponDto.getUserCouponId());
        findDto.copyMembers(userCouponDto, false);
        UserCouponEntity updateEntity =
                (UserCouponEntity) new UserCouponEntity().copyMembers(findDto, true);

        validateCoupon(updateEntity);
        validateCouponManager(updateEntity);
        UserCouponEntity updatedEntity = this.userCouponRepository.save(updateEntity);
        return (UserCouponDto) new UserCouponDto().copyMembers(updatedEntity, true);
    }

    public UserCouponDto deleteById(Long userCouponId, Long userId) {

        UserCouponEntity entity = this.userCouponRepository.findById(userCouponId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자 쿠폰을 찾을 수 없습니다."));
        entity.setUserId(userId);
        validateCouponManager(entity);
        this.userCouponRepository.delete(entity);
        return (UserCouponDto) new UserCouponDto().copyMembers(entity, true);
    }
    private void validateCouponManager(IUserCoupon userCoupon) {

        if (userCoupon.getUserId() == null) {
            throw new IllegalArgumentException("관리자 userId가 필요합니다.");
        }

        UserEntity user = this.userRepository.findById(userCoupon.getUserId())
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.SUPER_ADMIN) {
            throw new IllegalArgumentException("관리자만 사용자 쿠폰을 관리할 수 있습니다.");
        }
        userCoupon.setUser(user);
    }
    private void validateCoupon(IUserCoupon userCoupon) {
        if (userCoupon.getUserId() == null) {
            throw new IllegalArgumentException("사용자를 선택하세요.");
        }

        if (userCoupon.getCouponId() == null) {
            throw new IllegalArgumentException("쿠폰을 선택하세요.");
        }

        if (userCoupon.getIssuedAt() == null) {
            throw new IllegalArgumentException("사용자 쿠폰 발급날짜를 입력하세요");
        }

        if (userCoupon.getUserCouponStatus() == null) {
            throw new IllegalArgumentException("사용자 쿠폰 상태를 입력하세요.");
        }
    }
}
