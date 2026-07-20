package com.mjc.hotel.promotion;

public enum PromotionStateTypeEnum {
    /**
     * 현재 진행 중인 프로모션
     */
    ACTIVE,

    /**
     * 비활성화되어 적용되지 않는 프로모션
     */
    INACTIVE,

    /**
     * 종료되어 더 이상 사용할 수 없는 프로모션
     */
    EXPIRED
}
