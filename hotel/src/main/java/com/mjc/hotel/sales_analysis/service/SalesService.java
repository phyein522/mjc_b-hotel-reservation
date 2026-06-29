package com.mjc.hotel.sales_analysis.service;

import com.mjc.hotel.sales_analysis.dto.HotelResponseDto;
import com.mjc.hotel.sales_analysis.dto.SalesResponseDto;
import com.mjc.hotel.sales_analysis.entity.Hotel;
import com.mjc.hotel.sales_analysis.repository.HotelRepository;
import com.mjc.hotel.sales_analysis.repository.SalesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesService {

    private final SalesRepository salesRepository;
    private final HotelRepository hotelRepository;

    // 운영중인 호텔 목록 반환 (드롭다운용)
    public List<HotelResponseDto> getActiveHotelList() {
        return hotelRepository.findAllByIsActiveTrue()
                .stream()
                .map(HotelResponseDto::from)
                .collect(Collectors.toList());
    }

    // 호텔 선택 + 연도 → 올해/작년 매출 비교 결과 반환
    public SalesResponseDto getSalesSummary(Long hotelId, int year) {

        // 호텔 존재 여부 확인
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 호텔입니다. hotelId=" + hotelId));

        // 올해 매출 합산
        BigDecimal currentSales = salesRepository.sumSalesByHotelAndYear(hotelId, year);

        // 작년 매출 합산
        BigDecimal previousSales = salesRepository.sumSalesByHotelAndYear(hotelId, year - 1);

        // 증감률 계산
        double changeRate = calculateChangeRate(currentSales, previousSales);

        // 상승/하락/동일 판단
        String trend = decideTrend(changeRate);

        return SalesResponseDto.builder()
                .hotelId(hotelId)
                .hotelName(hotel.getHotelName())
                .year(year)
                .currentYearSales(currentSales)
                .previousYearSales(previousSales)
                .changeRate(changeRate)
                .trend(trend)
                .build();
    }

    // 증감률 계산: (올해 - 작년) / 작년 * 100
    // 작년 매출이 0이면 100% 처리
    private double calculateChangeRate(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    // 증감률 → 트렌드 문자열 변환
    private String decideTrend(double changeRate) {
        if (changeRate > 0) return "UP";
        if (changeRate < 0) return "DOWN";
        return "SAME";
    }
}
