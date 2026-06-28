package com.mjc.hotel.promotion;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class PromotionDto {
    private Long promotionId;
    private Long roomId;
    private String promotionName;
    private String promotionDesc;
    private String discountType;
    private String discountValue;
    private String startDate;
    private String endDate;
    private String resCount;
    private String conversionRate;
    private String status;
}
