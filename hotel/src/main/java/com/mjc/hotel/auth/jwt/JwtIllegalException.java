package com.mjc.hotel.auth.jwt;

public class JwtIllegalException extends RuntimeException {
    // unchecked exception 이 예외를 처리 하지 않아도 컴파일 에러 발생하지 않는다.
    // 데이터베이스 트랙잭션을 처리 할 수 있게 해준다. 성공일때는 모든 쿼리를 commit 실행해준다.
    // 실패하면 쿼리를 rollback 해준다.
    public JwtIllegalException(String message) {
        super(message);
    }
}
