package com.mjc.hotel.rates.service;

import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.hotels.HotelRepository;
import com.mjc.hotel.rates.dto.RatePolicyDto;
import com.mjc.hotel.rates.entity.RatePolicyEntity;
import com.mjc.hotel.rates.enums.ChildRateType;
import com.mjc.hotel.rates.repository.RatePolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;

@Service
@Transactional
public class RatePolicyService {

    @Autowired
    private RatePolicyRepository ratePolicyRepository;

    @Autowired
    private HotelRepository hotelRepository;

    public RatePolicyDto findByHotelId(Long hotelId) {
        RatePolicyEntity entity = ratePolicyRepository.findByHotel_HotelId(hotelId)
                .orElseGet(() -> createDefaultPolicy(hotelId));
        return convertToDto(entity);
    }

    public RatePolicyDto updateMinStay(Long hotelId, Integer minStayNights) {
        RatePolicyEntity entity = ratePolicyRepository.findByHotel_HotelId(hotelId)
                .orElseGet(() -> createDefaultPolicy(hotelId));
        
        entity.setMinStayNights(minStayNights);
        RatePolicyEntity saved = ratePolicyRepository.save(entity);
        return convertToDto(saved);
    }

    public RatePolicyDto updateTimes(Long hotelId, LocalTime checkInTime, LocalTime checkOutTime) {
        RatePolicyEntity entity = ratePolicyRepository.findByHotel_HotelId(hotelId)
                .orElseGet(() -> createDefaultPolicy(hotelId));

        entity.setCheckInTime(checkInTime);
        entity.setCheckOutTime(checkOutTime);
        RatePolicyEntity saved = ratePolicyRepository.save(entity);
        return convertToDto(saved);
    }

    public RatePolicyDto updateCancellation(Long hotelId, Integer cancelDeadlineDays, BigDecimal cancelFeeRate) {
        RatePolicyEntity entity = ratePolicyRepository.findByHotel_HotelId(hotelId)
                .orElseGet(() -> createDefaultPolicy(hotelId));

        entity.setCancelDeadlineDays(cancelDeadlineDays);
        entity.setCancelFeeRate(cancelFeeRate);
        RatePolicyEntity saved = ratePolicyRepository.save(entity);
        return convertToDto(saved);
    }

    public RatePolicyDto updateChildRates(Long hotelId, Integer freeChildAge, ChildRateType childRateType, BigDecimal childDiscountRate) {
        RatePolicyEntity entity = ratePolicyRepository.findByHotel_HotelId(hotelId)
                .orElseGet(() -> createDefaultPolicy(hotelId));

        if (childRateType == ChildRateType.DISCOUNT && (childDiscountRate == null || childDiscountRate.compareTo(BigDecimal.ZERO) < 0)) {
            throw new IllegalArgumentException("아동 할인율(DISCOUNT) 적용 시 올바른 할인율을 입력해야 합니다.");
        }

        entity.setFreeChildAge(freeChildAge);
        entity.setChildRateType(childRateType);
        entity.setChildDiscountRate(childRateType == ChildRateType.FREE ? null : childDiscountRate);
        RatePolicyEntity saved = ratePolicyRepository.save(entity);
        return convertToDto(saved);
    }

    private RatePolicyEntity createDefaultPolicy(Long hotelId) {
        HotelEntity hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다. ID: " + hotelId));

        RatePolicyEntity policy = RatePolicyEntity.builder()
                .minStayNights(1)
                .checkInTime(LocalTime.of(15, 0))
                .checkOutTime(LocalTime.of(11, 0))
                .cancelDeadlineDays(3)
                .cancelFeeRate(BigDecimal.valueOf(100.0)) // 기본 100% 수수료
                .freeChildAge(12)
                .childRateType(ChildRateType.FREE)
                .hotel(hotel)
                .build();
        
        return ratePolicyRepository.save(policy);
    }

    private RatePolicyDto convertToDto(RatePolicyEntity entity) {
        return RatePolicyDto.builder()
                .policyId(entity.getPolicyId())
                .minStayNights(entity.getMinStayNights())
                .checkInTime(entity.getCheckInTime())
                .checkOutTime(entity.getCheckOutTime())
                .cancelDeadlineDays(entity.getCancelDeadlineDays())
                .cancelFeeRate(entity.getCancelFeeRate())
                .freeChildAge(entity.getFreeChildAge())
                .childRateType(entity.getChildRateType())
                .childDiscountRate(entity.getChildDiscountRate())
                .hotelId(entity.getHotelId())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .build();
    }
}
