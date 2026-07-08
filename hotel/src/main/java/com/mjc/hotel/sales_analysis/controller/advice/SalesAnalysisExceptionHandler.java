package com.mjc.hotel.sales_analysis.controller.advice;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.sales_analysis.controller.SalesAnalysisController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = SalesAnalysisController.class)
public class SalesAnalysisExceptionHandler {

    // 1. 유효하지 않은 입력 데이터 예외 처리 (예: 날짜 형식 오류 시)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("[매출 분석 API] 잘못된 요청 파라미터 유입: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.make(ResponseCode.BAD_REQUEST, ex.getMessage(), null));
    }

    // 2. 기타 비즈니스 로직 처리 예외 통합 처리 (DB 장애 등)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
        log.error("[매출 분석 API] 연산 오류 발생", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.make(ResponseCode.SELECT_ERROR, "매출 분석 통계 조회에 실패했습니다.", ex.getMessage()));
    }
}
