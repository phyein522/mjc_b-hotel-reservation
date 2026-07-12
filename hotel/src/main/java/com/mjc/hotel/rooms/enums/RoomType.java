package com.mjc.hotel.rooms.enums;

import lombok.Getter;

@Getter
/**
 * 스탠다드/스위트/디럭스
 */
public enum RoomType {
	/**
	 * 스탠다드
	 */
	Standard(1)
	/**
	 * 스위트
	 */
	, Suite(2)
	/**
	 * 디럭스
	 */
	, Deluxe(3)
	/**
	 * 프리미엄
	 */
	, Premium(4)
	;
	;

	private final int value;

	RoomType(int value) {
		this.value = value;
	}
}
