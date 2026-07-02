package com.mjc.hotel.common;

import lombok.extern.slf4j.Slf4j;
import com.mjc.hotel.user.exception.DuplicateEmailException;
import com.mjc.hotel.user.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import tools.jackson.databind.exc.InvalidFormatException;

@Slf4j
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

	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<ApiResponse<Void>> handleMultipartException(MultipartException e) {
		// 진짜 원인을 로그로 확인
		Throwable rootCause = getRootCause(e);
		log.error("Multipart 파싱 실패. rootCause={}", rootCause.getMessage(), e);

		String message = switch (rootCause) {
			case MaxUploadSizeExceededException ex -> "업로드 가능한 파일 크기를 초과했습니다.";
			case InvalidFormatException ex -> "요청 데이터(JSON) 형식이 올바르지 않습니다.";
			default -> "요청 형식이 올바르지 않습니다. multipart/form-data 여부와 각 파트의 Content-Type을 확인해주세요.";
		};

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				ApiResponse.make(ResponseCode.BAD_REQUEST, message, null)
		);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ApiResponse<Void>> handleMaxUploadSize(MaxUploadSizeExceededException e) {
		log.warn("업로드 용량 초과: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
				ApiResponse.make(ResponseCode.PAYLOAD_TOO_LARGE, "파일 용량이 너무 큽니다.", null)
		);
	}

	private Throwable getRootCause(Throwable throwable) {
		Throwable cause = throwable;
		while (cause.getCause() != null && cause.getCause() != cause) {
			cause = cause.getCause();
		}
		return cause;
	}
}
