package com.mjc.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @EnableJpaAuditing  이 문장을 단위테스트 실행시 에러가 나서 별도의 JpaAuditingConfig 클래스로 분리함
@SpringBootApplication
public class HotelApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelApplication.class, args);
	}

}
