package com.mjc.hotel.rates.entity;

import com.mjc.hotel.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * rates 패키지 전용 호실 편의시설 옵션 Entity (1:1 매핑)
 * room_amenities 테이블에 대응됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
@Entity
@Table(name = "room_amenities")
public class RoomAmenityEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_amenity_id")
    private Long roomAmenityId;

    @Column(name = "wifi", nullable = false)
    @Builder.Default
    private Boolean wifi = false;

    @Column(name = "tv", nullable = false)
    @Builder.Default
    private Boolean tv = false;

    @Column(name = "bathtub", nullable = false)
    @Builder.Default
    private Boolean bathtub = false;

    @Column(name = "city_view", nullable = false)
    @Builder.Default
    private Boolean cityView = false;

    @Column(name = "ocean_view", nullable = false)
    @Builder.Default
    private Boolean oceanView = false;

    @Column(name = "breakfast_included", nullable = false)
    @Builder.Default
    private Boolean breakfastIncluded = false;

    @Column(name = "non_smoking", nullable = false)
    @Builder.Default
    private Boolean nonSmoking = false;

    @Column(name = "room_id", nullable = false, unique = true)
    private Long roomId;
}
