package com.mjc.hotel.usercoupon;

import com.mjc.hotel.coupon.CouponDto;
import com.mjc.hotel.coupon.CouponEntity;
import com.mjc.hotel.coupon.CouponRepository;
import com.mjc.hotel.coupon.ICoupon;
import com.mjc.hotel.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        validateCoupon(insertEntity);
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
        USerCouponEntity updateEntity =
                (UserCouponEntity) new UserCouponEntity().copyMembers(findDto, true);
        validateCoupon(updateEntity);
        validateCouponManager(updateEntity);
        UserCouponEntity updatedEntity = this.userCouponRepository.save(updateEntity);
        return (UserCouponDto) new UserCouponDto().copyMembers(updatedEntity, true);
    }
}
