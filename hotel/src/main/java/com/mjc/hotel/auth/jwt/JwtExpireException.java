package com.mjc.hotel.auth.jwt;

public class JwtExpireException extends RuntimeException {
    // checked exception 예외를 try~catch 든지 throws 든지 처리 하지 않으면 컴파일 에러 발생
    // 데이터베이스 트랙잭션을 처리 안 해준다. 성공이든 실패이든 commit 한다.
    public JwtExpireException(String message) {
        super(message);
    }
}
