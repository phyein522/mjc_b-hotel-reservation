package com.mjc.hotel.promotion;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="promotion")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PromotionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promotionId;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private String promotionName;

    @Column(nullable = false)
    private String promotionDesc;

    @Column(nullable = false)
    private String discountType;

    @Column(nullable = false)
    private String discountValue;

    @Column(nullable = false)
    private String startDate;

    private String endDate;
    private String resCount;
    private String conversionRate;
    private String status;

}
