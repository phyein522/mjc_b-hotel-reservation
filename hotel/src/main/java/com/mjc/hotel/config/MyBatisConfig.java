package com.mjc.hotel.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {
		"com.mjc.hotel.bookings.mapper",
		"com.mjc.hotel.sales_analysis.mapper",
		"com.mjc.hotel.user.mapper"
})
public class MyBatisConfig {
}
