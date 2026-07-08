package com.mjc.hotel.hotelstrans;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.hotels.IHotel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transportation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelTransEntity extends BaseEntity implements IHotelTrans {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transId;

    @Column(name = "trans_name")
    private String name;

    @Column(name = "trans_time")
    private String time;

    @Column(name = "trans_depart")
    private String depart;

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
    @Override
    public void setHotel(IHotel hotel) {

        if (hotel == null) {
            return;
        }

        if (this.hotel == null) {
            this.hotel = new HotelEntity();
        }

        this.hotel.copyMembers(hotel, true);
        this.hotelId = hotel.getHotelId();
    }
}
