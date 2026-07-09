package com.mjc.hotel.rooms.dto;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.hotels.HotelDto;
import com.mjc.hotel.hotels.IHotel;
import com.mjc.hotel.rooms.enums.RoomBedOption;
import com.mjc.hotel.rooms.enums.RoomStatus;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rooms.enums.RoomViewOption;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RoomDto extends BaseDto implements IRoom {
	private Long roomId;
	private String name;
	private String number;
	private Integer floor;
	private Integer size;
	private BigDecimal basePrice;
	private Integer maxAdult;
	private Integer maxChild;
	private Boolean isActive;
	private RoomType roomType;
	private RoomStatus roomStatus;
	private RoomViewOption roomViewOption;
	private RoomBedOption roomBedOption;
	private Long hotelId;
	private HotelDto hotel;
	private LocalDate blockStartDate;
	private LocalDate blockEndDate;

	@Override
	public Long getHotelId() {
		if ( this.hotel == null ) {
			this.hotel = new HotelDto();
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
			this.hotel = new HotelDto();
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
			this.hotel = new HotelDto();
		}
		this.hotel.copyMembers(hotel, true);
		this.hotelId = hotel.getHotelId();
	}
}
