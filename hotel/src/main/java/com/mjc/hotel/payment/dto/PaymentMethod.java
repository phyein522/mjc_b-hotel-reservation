package com.mjc.hotel.payment.dto;

import lombok.Getter;

@Getter
public enum PaymentMethod {
	Cache(0)
	, Online(1)
	, CreditCard(2)
	, CheckCard(3)
	, BankTransfer(4)
	;

	private final int value;

	PaymentMethod(int value) {
		this.value = value;
	}
}
