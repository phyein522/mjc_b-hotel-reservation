package com.mjc.hotel.rooms.enums;

import lombok.Getter;

@Getter
/**
 * 예약가능/예약불가
 */
public enum RoomStatus {
	/**
	 * 예약가능
	 */
	EnableReservation(10)
	/**
	 * 예약불가
	 */
	, DisableReservation(20)
	;

	private final int value;

	RoomStatus(int value) {
		this.value = value;
	}
}
