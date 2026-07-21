package com.mjc.hotel.payment.dto;

import lombok.Getter;

@Getter
public enum PaymentStatus {
	Ready(0)
	, Paid(1)
	, Failed(2)
	, Cancelled(3)
	, PartialRefunced(4)
	, Refunded(5)
	;

	private final int value;

	PaymentStatus(int value) {
		this.value = value;
	}
}
