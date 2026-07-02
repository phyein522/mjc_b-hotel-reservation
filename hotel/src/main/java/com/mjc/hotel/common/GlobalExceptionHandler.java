package com.mjc.hotel.common;

import com.mjc.hotel.user.exception.DuplicateEmailException;
import com.mjc.hotel.user.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// 사용자 없음 404
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiResponse<String>> handleUserNotFound(UserNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.make(ResponseCode.OTHER_ERROR, "not_found", ex.getMessage()));
	}

	// 이메일 중복 409
	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<ApiResponse<String>> handleDuplicateEmail(DuplicateEmailException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiResponse.make(ResponseCode.OTHER_ERROR, "conflict", ex.getMessage()));
	}

	// @Valid 검증 실패 400
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<String>> handleValidation(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(err -> err.getField() + ": " + err.getDefaultMessage())
				.findFirst()
				.orElse("입력값이 올바르지 않습니다.");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.make(ResponseCode.OTHER_ERROR, "bad_request", message));
	}

	// 비밀번호 불일치 등 일반 IllegalArgumentException 400
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<String>> handleIllegalArgument(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.make(ResponseCode.OTHER_ERROR, "bad_request", ex.getMessage()));
	}

	// 그 외 모든 예외 500
	@ExceptionHandler(Throwable.class)
	public ResponseEntity<ApiResponse<String>> handleThrowable(Throwable ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.make(ResponseCode.OTHER_ERROR, "error", ex.getMessage()));
	}
}
