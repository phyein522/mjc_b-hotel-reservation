package com.mjc.hotel.hotelsattr;

import com.mjc.hotel.hotels.HotelEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attraction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelAttrEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attrId;

    @Column
    private String context;

    @Transient
    private Long hotelId;

    @JoinColumn(name = "hotel_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private HotelEntity hotel;
}
