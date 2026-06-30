package com.mjc.hotel.common;

public enum ResponseCode {
	SUCCESS(10000)
	, INSERT_OK(21000)
	, UPDATE_OK(22000)
	, DELETE_OK(23000)
	, SELECT_OK(24000)
	, INSERT_ERROR(41000)
	, UPDATE_ERROR(42000)
	, DELETE_ERROR(43000)
	, SELECT_ERROR(44000)
	, OTHER_ERROR(49000)
	, DATA_NOT_FOUND_ERROR(51000)
	, AUTHENTICATION_ERROR(52000) // 로그인 인증 에러
	, AUTHORIZATION_ERROR(53000) // 자원을 사용하기 위한 권한 인가 에러
	, TOKEN_NOT_EXPIRED_ERROR(54000) // 토큰이 정상인데도 리프레시를 발급할때
	, TOKEN_EXPIRED_ERROR(55000) // 토큰 유효기간 끝남
	;

	private Integer code;

	ResponseCode(Integer code) {
		this.code = code;
	}
}
