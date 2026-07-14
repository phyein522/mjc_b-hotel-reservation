package com.mjc.hotel.rates.entity;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rates.enums.SeasonStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "season_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SeasonRateEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "season_rate_id")
    private Long seasonRateId;

    @Column(name = "season_name", nullable = false, length = 50)
    private String seasonName;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "weekday_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal weekdayPrice;

    @Column(name = "weekend_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal weekendPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SeasonStatus status;

    @Builder.Default
    @Column(name = "min_stay_nights", nullable = false)
    private Integer minStayNights = 1;

    @Builder.Default
    @Column(name = "weekday_policy_enabled", nullable = false)
    private Boolean weekdayPolicyEnabled = true;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "room_type")
    private RoomType roomType;

    @Transient
    private Long hotelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotel;

    @Transient
    private Long policyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private RatePolicyEntity policy;

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

    public Long getPolicyId() {
        if (this.policy != null) {
            return this.policy.getPolicyId();
        }
        return this.policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
        if (this.policy == null) {
            this.policy = new RatePolicyEntity();
        }
        this.policy.setPolicyId(policyId);
    }

    // Helper to calculate status dynamically based on current date
    public void updateStatusByDate(LocalDate today) {
        if (today.isBefore(this.startDate)) {
            this.status = SeasonStatus.UPCOMING;
        } else if (today.isAfter(this.endDate)) {
            this.status = SeasonStatus.ENDED;
        } else {
            this.status = SeasonStatus.ONGOING;
        }
    }
}
