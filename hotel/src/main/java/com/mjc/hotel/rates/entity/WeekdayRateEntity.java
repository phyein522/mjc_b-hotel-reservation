package com.mjc.hotel.rates.entity;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.rates.enums.DayType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "weekday_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WeekdayRateEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weekday_rate_id")
    private Long weekdayRateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_type", nullable = false, length = 20)
    private DayType dayType;

    @Column(name = "rate_multiplier_percent", precision = 5, scale = 2)
    private BigDecimal rateMultiplierPercent;

    @Transient
    private Long seasonRateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_rate_id", nullable = false)
    private SeasonRateEntity seasonRate;

    public Long getSeasonRateId() {
        if (this.seasonRate != null) {
            return this.seasonRate.getSeasonRateId();
        }
        return this.seasonRateId;
    }

    public void setSeasonRateId(Long seasonRateId) {
        this.seasonRateId = seasonRateId;
        if (this.seasonRate == null) {
            this.seasonRate = new SeasonRateEntity();
        }
        this.seasonRate.setSeasonRateId(seasonRateId);
    }
}
