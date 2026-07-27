package com.mjc.hotel.rates.entity;

import com.mjc.hotel.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

/**
 * rates 패키지 전용 호실 상세 설명 Entity (1:1 매핑)
 * room_details 테이블에 대응됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
@Entity
@Table(name = "room_details")
@Comment("호텔 상세 설명 테이블")
public class RoomDetailEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_detail_id")
    @Comment("호실 상세 정보 PK")
    private Long roomDetailId;

    @Column(name = "description", columnDefinition = "TEXT")
    @Comment("호실에 대한 상세 설명 텍스트")
    private String description;

    @Column(name = "room_id", nullable = false, unique = true)
    @Comment("연결된 room 테이블의 PK (1:1 관계)")
    private Long roomId;
}
