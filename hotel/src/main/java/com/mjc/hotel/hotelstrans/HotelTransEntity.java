package com.mjc.hotel.hotelstrans;

import com.mjc.hotel.hotels.HotelEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transportation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelTransEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transId;

    @Column(name = "trans_name")
    private String transName;

    @Column(name = "trans_time")
    private Integer transTime;

    @Column(name = "trans_depart")
    private String transDepart;

    @Transient
    private Long hotelId;

    @JoinColumn(name = "hotel_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private HotelEntity hotel;
}
