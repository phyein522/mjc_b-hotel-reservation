package com.mjc.hotel.rooms.dto;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.hotels.IHotel;
import com.mjc.hotel.rooms.enums.RoomBedOption;
import com.mjc.hotel.rooms.enums.RoomStatus;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rooms.enums.RoomViewOption;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "rooms", comment = "객실 정보")
public class RoomEntity extends BaseEntity implements IRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "room_id", comment = "기본키")
	private Long roomId;

	@Column(nullable = false, length = 50, comment = "객실 이름")
	private String name;

	@Column(nullable = false, length = 50, comment = "번호")
	private String number;

	@Column(nullable = false, comment = "층")
	private Integer floor;

	@Column(nullable = false, comment = "크기")
	private Integer size;

	@Column(name = "base_price", nullable = false, comment = "기본가격")
	private BigDecimal basePrice;

	@Column(name = "max_adult", nullable = false, comment = "성인최대수")
	private Integer maxAdult;

	@Column(name = "max_child", nullable = false, comment = "어린이최대수")
	private Integer maxChild;

	@Column(name = "is_active", nullable = false, comment = "사용가능")
	private Boolean isActive;

	@Column(name = "room_type", nullable = false, comment = "객실타입(Standard/Sweet/Deluxe")
	private RoomType roomType;

	@Column(name = "room_status", nullable = false, comment = "상태(EnableReservation/DisableReservation)")
	private RoomStatus roomStatus;

	@Column(name = "room_view_option", nullable = false, comment = "전망(CityView/RiverView/MountainView/OceanView)")
	private RoomViewOption roomViewOption;

	@Column(name = "room_bed_option", nullable = false, comment = "침대(Floor/DoubleBed/QueenBed)")
	private RoomBedOption roomBedOption;

	@Transient
	private Long hotelId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hotel_id", nullable = false, comment = "호텔외래키")
	private HotelEntity hotel;

	@Override
	public Long getHotelId() {
		if ( this.hotel == null ) {
			this.hotel = new HotelEntity();
		}
		if ( this.hotel.getHotelId() != null) {
			this.hotelId = this.hotel.getHotelId();
		} else {
			this.hotel.setHotelId(this.hotelId);
		}
		return this.hotel.getHotelId();
	}

	@Override
	public void setHotelId(Long hotelId) {
		// Long 외래키값 과 객체.기본키 값을 항상 같도록 해야 한다.
		if ( this.hotel == null ) {
			this.hotel = new HotelEntity();
		}
		this.hotel.setHotelId(hotelId);
		this.hotelId = hotelId;
	}

	@Override
	public void setHotel(IHotel hotel) {
		// Long 외래키값 과 객체.기본키 값을 항상 같도록 해야 한다.
		if ( hotel == null ) {
			return;
		}
		if ( this.hotel == null ) {
			this.hotel = new HotelEntity();
		}
		this.hotel.copyMembers(hotel, true);
		this.hotelId = hotel.getHotelId();
	}
}
