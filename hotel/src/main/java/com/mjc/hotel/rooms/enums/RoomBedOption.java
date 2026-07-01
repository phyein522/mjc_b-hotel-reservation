package com.mjc.hotel.rooms.enums;

import lombok.Getter;

@Getter
public enum RoomBedOption {
	/**
	 * 온돌
	 */
	Floor(100)
	/**
	 * 더블침대
	 */
	, DoubleBed(200)
	/**
	 * 퀸침대
	 */
	, QueenBed(300)
	;

	private final int value;

	RoomBedOption(int value) {
		this.value = value;
	}
}
