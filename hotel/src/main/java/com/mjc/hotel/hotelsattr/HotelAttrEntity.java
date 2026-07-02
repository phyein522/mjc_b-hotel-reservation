package com.mjc.hotel.hotelsattr;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.hotels.HotelEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hotel_attraction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelAttrEntity extends BaseEntity implements IHotelAttr {
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

    @Override
    public Long getHotelId() {

        if (hotel != null) {
            return hotel.getHotelId();
        }

        return hotelId;
    }

    @Override
    public void setHotelId(Long hotelId) {

        this.hotelId = hotelId;

        if (hotel == null) {
            hotel = new HotelEntity();
        }

        hotel.setHotelId(hotelId);
    }
}
