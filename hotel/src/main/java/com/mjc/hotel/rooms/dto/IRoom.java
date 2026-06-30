package com.mjc.hotel.rooms.dto;

import com.mjc.hotel.rooms.enums.RoomBedOption;
import com.mjc.hotel.rooms.enums.RoomStatus;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rooms.enums.RoomViewOption;

import java.math.BigDecimal;

public interface IRoom {
	Long getRoomId();
	void setRoomId(Long roomId);

	String getName();
	void setName(String name);

	String getNumber();
	void setNumber(String number);

	Integer getFloor();
	void setFloor(Integer floor);

	Integer getSize();
	void setSize(Integer size);

	BigDecimal getBasePrice();
	void setBasePrice(BigDecimal basePrice);

	Integer getMaxAdult();
	void setMaxAdult(Integer maxAdult);

	Integer getMaxChild();
	void setMaxChild(Integer maxChild);

	Boolean getIsActive();
	void setIsActive(Boolean isActive);

	RoomType getRoomType();
	void setRoomType(RoomType roomType);

	RoomStatus getRoomStatus();
	void setRoomStatus(RoomStatus roomStatus);

	RoomViewOption getRoomViewOption();
	void setRoomViewOption(RoomViewOption roomViewOption);

	RoomBedOption getRoomBedOption();
	void setRoomBedOption(RoomBedOption roomBedOption);

	Long getHotelId();
	void setHotelId(Long hotelId);

	default IRoom copyMembers(IRoom source, boolean forced) {
		if ( source == null ) {
			return this;
		}
		if ( forced || source.getRoomId() != null ) {
			this.setRoomId(source.getRoomId());
		}
		if ( forced || source.getName() != null ) {
			this.setName(source.getName());
		}
		if ( forced || source.getNumber() != null ) {
			this.setNumber(source.getNumber());
		}
		if ( forced || source.getFloor() != null ) {
			this.setFloor(source.getFloor());
		}
		if ( forced || source.getSize() != null ) {
			this.setSize(source.getSize());
		}
		if ( forced || source.getBasePrice() != null ) {
			this.setBasePrice(source.getBasePrice());
		}
		if ( forced || source.getMaxAdult() != null ) {
			this.setMaxAdult(source.getMaxAdult());
		}
		if ( forced || source.getMaxChild() != null ) {
			this.setMaxChild(source.getMaxChild());
		}
		if ( forced || source.getIsActive() != null ) {
			this.setIsActive(source.getIsActive());
		}
		if ( forced || source.getRoomType() != null ) {
			this.setRoomType(source.getRoomType());
		}
		if ( forced || source.getRoomStatus() != null ) {
			this.setRoomStatus(source.getRoomStatus());
		}
		if ( forced || source.getRoomViewOption() != null ) {
			this.setRoomViewOption(source.getRoomViewOption());
		}
		if ( forced || source.getRoomBedOption() != null ) {
			this.setRoomBedOption(source.getRoomBedOption());
		}
		if ( forced || source.getHotelId() != null ) {
			this.setHotelId(source.getHotelId());
		}
		return this;
	}
}
