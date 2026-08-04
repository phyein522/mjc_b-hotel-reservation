package com.mjc.hotel.usercoupon;

import com.mjc.hotel.coupon.CouponEntity;
import com.mjc.hotel.coupon.CouponStatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestUserCouponPaymentUse {

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private UserCouponService userCouponService;

    @Test
    void useForPayment_marksAvailableCouponAsUsed() {
        CouponEntity coupon = CouponEntity.builder()
                .couponId(10L)
                .status(CouponStatusEnum.ACTIVE)
                .expirationDate(LocalDate.now().plusDays(1))
                .build();
        UserCouponEntity userCoupon = UserCouponEntity.builder()
                .userCouponId(20L)
                .coupon(coupon)
                .userCouponStatus(UserCouponStatusEnum.AVAILABLE)
                .build();

        when(userCouponRepository.findByUser_UserIdAndCoupon_CouponId(3L, 10L))
                .thenReturn(Optional.of(userCoupon));
        when(userCouponRepository.save(any(UserCouponEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserCouponDto result = userCouponService.useForPayment(3L, 10L, 30L);

        assertEquals(UserCouponStatusEnum.USED, result.getUserCouponStatus());
        assertEquals(30L, result.getUsedPaymentId());
        verify(userCouponRepository).save(userCoupon);
    }

    @Test
    void validateAvailableForPayment_rejectsExpiredCoupon() {
        CouponEntity coupon = CouponEntity.builder()
                .couponId(10L)
                .status(CouponStatusEnum.ACTIVE)
                .expirationDate(LocalDate.now().minusDays(1))
                .build();
        UserCouponEntity userCoupon = UserCouponEntity.builder()
                .userCouponId(20L)
                .coupon(coupon)
                .userCouponStatus(UserCouponStatusEnum.AVAILABLE)
                .build();

        when(userCouponRepository.findByUser_UserIdAndCoupon_CouponId(3L, 10L))
                .thenReturn(Optional.of(userCoupon));

        assertThrows(IllegalArgumentException.class,
                () -> userCouponService.validateAvailableForPayment(3L, 10L));
        verify(userCouponRepository, never()).save(any());
    }
}
