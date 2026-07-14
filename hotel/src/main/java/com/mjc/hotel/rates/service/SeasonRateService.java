package com.mjc.hotel.rates.service;

import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.hotels.HotelRepository;
import com.mjc.hotel.rates.dto.PricePreviewDto;
import com.mjc.hotel.rates.dto.SeasonRateDto;
import com.mjc.hotel.rates.entity.RatePolicyEntity;
import com.mjc.hotel.rates.entity.SeasonRateEntity;
import com.mjc.hotel.rates.entity.WeekdayRateEntity;
import com.mjc.hotel.rates.enums.DayType;
import com.mjc.hotel.rates.enums.SeasonStatus;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rates.repository.RatePolicyRepository;
import com.mjc.hotel.rates.repository.SeasonRateRepository;
import com.mjc.hotel.rates.repository.WeekdayRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SeasonRateService {

    @Autowired
    private SeasonRateRepository seasonRateRepository;

    @Autowired
    private WeekdayRateRepository weekdayRateRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RatePolicyRepository ratePolicyRepository;

    public List<SeasonRateDto> getSeasonsByHotelAndRoomType(Long hotelId, RoomType roomType) {
        List<SeasonRateEntity> entities = seasonRateRepository.findByHotel_HotelIdAndRoomType(hotelId, roomType);
        LocalDate today = LocalDate.now();
        return entities.stream().map(entity -> {
            entity.updateStatusByDate(today);
            return convertToDto(entity);
        }).toList();
    }

    public SeasonRateDto getSeasonById(Long seasonRateId) {
        SeasonRateEntity entity = seasonRateRepository.findById(seasonRateId)
                .orElseThrow(() -> new IllegalArgumentException("시즌 요금을 찾을 수 없습니다. ID: " + seasonRateId));
        entity.updateStatusByDate(LocalDate.now());
        return convertToDto(entity);
    }

    public SeasonRateDto createSeason(SeasonRateDto dto, BigDecimal weekendMultiplier) {
        validateDateOverlap(dto.getHotelId(), dto.getRoomType(), dto.getStartDate(), dto.getEndDate(), null);

        HotelEntity hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다. ID: " + dto.getHotelId()));

        RatePolicyEntity policy = ratePolicyRepository.findByHotel_HotelId(dto.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("호텔의 요금 정책이 설정되어 있지 않습니다."));

        BigDecimal calculatedWeekendPrice = calculateWeekendPrice(
                dto.getWeekdayPrice(),
                dto.getWeekendPrice(),
                dto.getWeekdayPolicyEnabled(),
                weekendMultiplier
        );

        SeasonRateEntity entity = SeasonRateEntity.builder()
                .seasonName(dto.getSeasonName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .weekdayPrice(dto.getWeekdayPrice())
                .weekendPrice(calculatedWeekendPrice)
                .minStayNights(dto.getMinStayNights() != null ? dto.getMinStayNights() : 1)
                .weekdayPolicyEnabled(dto.getWeekdayPolicyEnabled() != null ? dto.getWeekdayPolicyEnabled() : true)
                .roomType(dto.getRoomType())
                .hotel(hotel)
                .policy(policy)
                .build();

        entity.updateStatusByDate(LocalDate.now());
        SeasonRateEntity saved = seasonRateRepository.save(entity);

        // Save Weekday rates for this season
        saveOrUpdateWeekdayRates(saved, weekendMultiplier);

        return convertToDto(saved);
    }

    public SeasonRateDto updateSeason(Long seasonRateId, SeasonRateDto dto, BigDecimal weekendMultiplier) {
        SeasonRateEntity entity = seasonRateRepository.findById(seasonRateId)
                .orElseThrow(() -> new IllegalArgumentException("시즌 요금을 찾을 수 없습니다. ID: " + seasonRateId));

        validateDateOverlap(entity.getHotel().getHotelId(), dto.getRoomType(), dto.getStartDate(), dto.getEndDate(), seasonRateId);

        BigDecimal calculatedWeekendPrice = calculateWeekendPrice(
                dto.getWeekdayPrice(),
                dto.getWeekendPrice(),
                dto.getWeekdayPolicyEnabled(),
                weekendMultiplier
        );

        entity.setSeasonName(dto.getSeasonName());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setWeekdayPrice(dto.getWeekdayPrice());
        entity.setWeekendPrice(calculatedWeekendPrice);
        entity.setMinStayNights(dto.getMinStayNights() != null ? dto.getMinStayNights() : 1);
        entity.setWeekdayPolicyEnabled(dto.getWeekdayPolicyEnabled() != null ? dto.getWeekdayPolicyEnabled() : true);
        entity.setRoomType(dto.getRoomType());

        entity.updateStatusByDate(LocalDate.now());
        SeasonRateEntity saved = seasonRateRepository.save(entity);

        saveOrUpdateWeekdayRates(saved, weekendMultiplier);

        return convertToDto(saved);
    }

    public void deleteSeason(Long seasonRateId) {
        SeasonRateEntity entity = seasonRateRepository.findById(seasonRateId)
                .orElseThrow(() -> new IllegalArgumentException("시즌 요금을 찾을 수 없습니다. ID: " + seasonRateId));

        // Delete associated weekday rates first
        List<WeekdayRateEntity> weekdayRates = weekdayRateRepository.findBySeasonRate_SeasonRateId(seasonRateId);
        weekdayRateRepository.deleteAll(weekdayRates);

        seasonRateRepository.delete(entity);
    }

    public PricePreviewDto calculatePreview(PricePreviewDto previewDto) {
        BigDecimal weekdayPrice = previewDto.getWeekdayPrice();
        if (weekdayPrice == null) {
            throw new IllegalArgumentException("평일 기준요금은 필수입니다.");
        }

        BigDecimal multiplier = previewDto.getMultiplier() != null ? previewDto.getMultiplier() : BigDecimal.valueOf(20.0);
        BigDecimal weekendPrice;

        if (Boolean.TRUE.equals(previewDto.getWeekdayPolicyEnabled())) {
            BigDecimal factor = BigDecimal.ONE.add(multiplier.divide(BigDecimal.valueOf(100.0), 4, RoundingMode.HALF_UP));
            weekendPrice = weekdayPrice.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        } else {
            if (previewDto.getWeekendPrice() != null) {
                weekendPrice = previewDto.getWeekendPrice();
            } else {
                weekendPrice = weekdayPrice.multiply(BigDecimal.valueOf(1.2)).setScale(2, RoundingMode.HALF_UP);
            }
        }

        BigDecimal increaseRate = BigDecimal.ZERO;
        if (weekdayPrice.compareTo(BigDecimal.ZERO) > 0) {
            increaseRate = weekendPrice.subtract(weekdayPrice)
                    .divide(weekdayPrice, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100.0))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return PricePreviewDto.builder()
                .weekdayPrice(weekdayPrice)
                .weekendPrice(previewDto.getWeekendPrice())
                .weekdayPolicyEnabled(previewDto.getWeekdayPolicyEnabled())
                .multiplier(multiplier)
                .calculatedWeekdayPrice(weekdayPrice)
                .calculatedWeekendPrice(weekendPrice)
                .increaseRate(increaseRate)
                .build();
    }

    private void validateDateOverlap(Long hotelId, RoomType roomType, LocalDate startDate, LocalDate endDate, Long currentSeasonRateId) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("시작일과 종료일은 필수입니다.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다.");
        }

        List<SeasonRateEntity> existingSeasons = seasonRateRepository.findByHotel_HotelIdAndRoomType(hotelId, roomType);
        for (SeasonRateEntity existing : existingSeasons) {
            if (currentSeasonRateId != null && existing.getSeasonRateId().equals(currentSeasonRateId)) {
                continue;
            }

            // Overlap check: (start1 <= end2) && (end1 >= start2)
            boolean overlaps = (startDate.isBefore(existing.getEndDate()) || startDate.isEqual(existing.getEndDate()))
                    && (endDate.isAfter(existing.getStartDate()) || endDate.isEqual(existing.getStartDate()));

            if (overlaps) {
                throw new IllegalArgumentException(String.format("입력한 기간이 기존 시즌 '%s' (%s ~ %s)과 겹칩니다.",
                        existing.getSeasonName(), existing.getStartDate(), existing.getEndDate()));
            }
        }
    }

    private BigDecimal calculateWeekendPrice(BigDecimal weekdayPrice, BigDecimal weekendPrice, Boolean policyEnabled, BigDecimal multiplier) {
        if (weekdayPrice == null) {
            throw new IllegalArgumentException("평일 기준요금은 필수입니다.");
        }
        if (Boolean.TRUE.equals(policyEnabled)) {
            BigDecimal mult = multiplier != null ? multiplier : BigDecimal.valueOf(20.0);
            BigDecimal factor = BigDecimal.ONE.add(mult.divide(BigDecimal.valueOf(100.0), 4, RoundingMode.HALF_UP));
            return weekdayPrice.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        } else {
            if (weekendPrice != null) {
                return weekendPrice;
            }
            return weekdayPrice.multiply(BigDecimal.valueOf(1.2)).setScale(2, RoundingMode.HALF_UP);
        }
    }

    private void saveOrUpdateWeekdayRates(SeasonRateEntity season, BigDecimal weekendMultiplier) {
        List<WeekdayRateEntity> existing = weekdayRateRepository.findBySeasonRate_SeasonRateId(season.getSeasonRateId());

        WeekdayRateEntity weekdayRate = existing.stream()
                .filter(r -> r.getDayType() == DayType.WEEKDAY)
                .findFirst()
                .orElseGet(() -> WeekdayRateEntity.builder().dayType(DayType.WEEKDAY).seasonRate(season).build());
        weekdayRate.setRateMultiplierPercent(BigDecimal.ZERO);
        weekdayRateRepository.save(weekdayRate);

        WeekdayRateEntity weekendRate = existing.stream()
                .filter(r -> r.getDayType() == DayType.WEEKEND)
                .findFirst()
                .orElseGet(() -> WeekdayRateEntity.builder().dayType(DayType.WEEKEND).seasonRate(season).build());
        
        BigDecimal mult = weekendMultiplier != null ? weekendMultiplier : BigDecimal.valueOf(20.0);
        weekendRate.setRateMultiplierPercent(mult);
        weekdayRateRepository.save(weekendRate);
    }

    private SeasonRateDto convertToDto(SeasonRateEntity entity) {
        return SeasonRateDto.builder()
                .seasonRateId(entity.getSeasonRateId())
                .seasonName(entity.getSeasonName())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .weekdayPrice(entity.getWeekdayPrice())
                .weekendPrice(entity.getWeekendPrice())
                .status(entity.getStatus())
                .minStayNights(entity.getMinStayNights())
                .weekdayPolicyEnabled(entity.getWeekdayPolicyEnabled())
                .roomType(entity.getRoomType())
                .hotelId(entity.getHotelId())
                .policyId(entity.getPolicyId())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .build();
    }
}
