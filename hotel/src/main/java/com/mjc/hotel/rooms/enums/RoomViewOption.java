package com.mjc.hotel.rooms.enums;

import lombok.Getter;

@Getter
/**
 * 도시전망/강전망/산전망/바다전망
 */
public enum RoomViewOption {
	/**
	 * 도시전망
	 */
	CityView(1000)
	/**
	 * 강전망
	 */
	, RiverView(2000)
	/**
	 * 산전망
	 */
	, MountainView(3000)
	/**
	 * 바다전망
	 */
	, OceanView(4000)
	;

	private final int value;

	RoomViewOption(int value) {
		this.value = value;
	}
}
