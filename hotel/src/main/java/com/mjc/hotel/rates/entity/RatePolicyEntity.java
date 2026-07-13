package com.mjc.hotel.rates.entity;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.rates.enums.ChildRateType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "rate_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RatePolicyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long policyId;

    @Builder.Default
    @Column(name = "min_stay_nights", nullable = false)
    private Integer minStayNights = 1;

    @Builder.Default
    @Column(name = "check_in_time", nullable = false)
    private LocalTime checkInTime = LocalTime.of(15, 0);

    @Builder.Default
    @Column(name = "check_out_time", nullable = false)
    private LocalTime checkOutTime = LocalTime.of(11, 0);

    @Builder.Default
    @Column(name = "cancel_deadline_days", nullable = false)
    private Integer cancelDeadlineDays = 3;

    @Builder.Default
    @Column(name = "cancel_fee_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal cancelFeeRate = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "free_child_age", nullable = false)
    private Integer freeChildAge = 12;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "child_rate_type", nullable = false)
    private ChildRateType childRateType = ChildRateType.FREE;

    @Column(name = "child_discount_rate", precision = 5, scale = 2)
    private BigDecimal childDiscountRate;

    @Transient
    private Long hotelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotel;

    public Long getHotelId() {
        if (this.hotel != null) {
            return this.hotel.getHotelId();
        }
        return this.hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
        if (this.hotel == null) {
            this.hotel = new HotelEntity();
        }
        this.hotel.setHotelId(hotelId);
    }
}
