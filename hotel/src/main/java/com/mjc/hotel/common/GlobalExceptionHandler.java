package com.mjc.hotel.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<ApiResponse<String>> handlerThrowable(Throwable ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(
				ApiResponse.make(ResponseCode.OTHER_ERROR, "error", ex.getMessage())
			);
	}
}
