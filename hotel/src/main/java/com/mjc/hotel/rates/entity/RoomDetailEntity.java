package com.mjc.hotel.rates.entity;

import com.mjc.hotel.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
public class RoomDetailEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_detail_id")
    private Long roomDetailId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "room_id", nullable = false, unique = true)
    private Long roomId;
}
