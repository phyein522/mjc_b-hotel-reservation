package com.mjc.hotel.rates.entity;

import com.mjc.hotel.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

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
@Comment("호실 편의시설 옵션 테이블")
public class RoomAmenityEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_amenity_id")
    @Comment("호실 편의시설 PK")
    private Long roomAmenityId;

    @Column(name = "wifi", nullable = false)
    @Comment("와이파이 제공 여부")
    @Builder.Default
    private Boolean wifi = false;

    @Column(name = "tv", nullable = false)
    @Comment("TV 제공 여부")
    @Builder.Default
    private Boolean tv = false;

    @Column(name = "bathtub", nullable = false)
    @Comment("욕조 제공 여부")
    @Builder.Default
    private Boolean bathtub = false;

    @Column(name = "city_view", nullable = false)
    @Comment("시티뷰 제공 여부")
    @Builder.Default
    private Boolean cityView = false;

    @Column(name = "ocean_view", nullable = false)
    @Comment("오션뷰 제공 여부")
    @Builder.Default
    private Boolean oceanView = false;

    @Column(name = "breakfast_included", nullable = false)
    @Comment("조식 포함 여부")
    @Builder.Default
    private Boolean breakfastIncluded = false;

    @Column(name = "non_smoking", nullable = false)
    @Comment("금연실 여부")
    @Builder.Default
    private Boolean nonSmoking = false;

    @Column(name = "room_id", nullable = false, unique = true)
    @Comment("연결된 room 테이블의 PK (1:1 관계)")
    private Long roomId;
}
