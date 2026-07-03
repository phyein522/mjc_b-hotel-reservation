package com.mjc.hotel.sales_analysis.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "room_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FakeRoomType {
    @Id
    @Column(name = "room_type_id")
    private Long roomTypeId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;
}
