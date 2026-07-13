package com.mjc.hotel.rates.service;

import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.hotels.HotelRepository;
import com.mjc.hotel.rates.dto.RateSummaryResponseDto;
import com.mjc.hotel.rates.entity.SeasonRateEntity;
import com.mjc.hotel.rates.repository.SeasonRateRepository;
import com.mjc.hotel.rooms.dto.RoomEntity;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rooms.service.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RateSummaryService {

    @Autowired
    private SeasonRateRepository seasonRateRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    public RateSummaryResponseDto getRateSummary(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new IllegalArgumentException("호텔을 찾을 수 없습니다. ID: " + hotelId);
        }

        LocalDate today = LocalDate.now();

        // 1. Fetch active rooms
        HotelEntity hotel = new HotelEntity();
        hotel.setHotelId(hotelId);
        List<RoomEntity> activeRooms = roomRepository.findAllByHotelEquals(hotel, PageRequest.of(0, 1000))
                .getContent().stream()
                .filter(RoomEntity::getIsActive)
                .toList();

        // 2. Fetch all seasons for this hotel
        List<SeasonRateEntity> seasons = seasonRateRepository.findByHotel_HotelId(hotelId);

        // 3. Calculate Average Room Price & Change Rate
        BigDecimal averageBasePrice = calculateAverageRoomPriceForMonth(activeRooms, seasons, today);
        BigDecimal lastMonthAverage = calculateAverageRoomPriceForMonth(activeRooms, seasons, today.minusMonths(1));
        BigDecimal priceChangeRate = BigDecimal.ZERO;
        if (lastMonthAverage.compareTo(BigDecimal.ZERO) > 0) {
            priceChangeRate = averageBasePrice.subtract(lastMonthAverage)
                    .divide(lastMonthAverage, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100.0))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // 4. Current Season Info (today)
        SeasonRateEntity currentStandardSeason = seasons.stream()
                .filter(s -> s.getRoomType() == RoomType.Standard)
                .filter(s -> !today.isBefore(s.getStartDate()) && !today.isAfter(s.getEndDate()))
                .findFirst()
                .orElse(null);

        String currentSeasonName = currentStandardSeason != null ? currentStandardSeason.getSeasonName() : "평시";
        BigDecimal currentStandardWeekdayPrice = currentStandardSeason != null ? currentStandardSeason.getWeekdayPrice() : 
                activeRooms.stream().filter(r -> r.getRoomType() == RoomType.Standard).map(RoomEntity::getBasePrice).filter(Objects::nonNull).findFirst().orElse(BigDecimal.ZERO);

        // Calculate off-season standard price
        SeasonRateEntity offSeasonStandard = seasons.stream()
                .filter(s -> s.getRoomType() == RoomType.Standard)
                .filter(s -> s.getSeasonName().contains("비수기") || s.getSeasonName().toLowerCase().contains("off"))
                .findFirst()
                .orElse(null);

        BigDecimal offSeasonPrice = offSeasonStandard != null ? offSeasonStandard.getWeekdayPrice() : 
                activeRooms.stream().filter(r -> r.getRoomType() == RoomType.Standard).map(RoomEntity::getBasePrice).filter(Objects::nonNull).findFirst().orElse(BigDecimal.ZERO);

        BigDecimal increaseRateComparedToOffSeason = BigDecimal.ZERO;
        if (offSeasonPrice.compareTo(BigDecimal.ZERO) > 0 && currentStandardWeekdayPrice.compareTo(BigDecimal.ZERO) > 0) {
            increaseRateComparedToOffSeason = currentStandardWeekdayPrice.subtract(offSeasonPrice)
                    .divide(offSeasonPrice, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100.0))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // 5. Next Season Info
        List<SeasonRateEntity> upcomingSeasons = seasons.stream()
                .filter(s -> s.getStartDate().isAfter(today))
                .sorted((s1, s2) -> s1.getStartDate().compareTo(s2.getStartDate()))
                .toList();

        LocalDate nextSeasonStartDate = null;
        Long nextSeasonDDay = null;
        Integer nextSeasonRoomTypeCount = 0;

        if (!upcomingSeasons.isEmpty()) {
            SeasonRateEntity nextSeason = upcomingSeasons.get(0);
            final LocalDate targetStartDate = nextSeason.getStartDate();
            nextSeasonStartDate = targetStartDate;
            nextSeasonDDay = ChronoUnit.DAYS.between(today, nextSeasonStartDate);
            
            // Count unique room types starting on the same date
            nextSeasonRoomTypeCount = (int) upcomingSeasons.stream()
                    .filter(s -> s.getStartDate().isEqual(targetStartDate))
                    .map(SeasonRateEntity::getRoomType)
                    .filter(Objects::nonNull)
                    .distinct()
                    .count();
        }

        return RateSummaryResponseDto.builder()
                .averageBasePrice(averageBasePrice.setScale(2, RoundingMode.HALF_UP))
                .priceChangeRate(priceChangeRate)
                .currentSeasonName(currentSeasonName)
                .currentStandardWeekdayPrice(currentStandardWeekdayPrice.setScale(2, RoundingMode.HALF_UP))
                .increaseRateComparedToOffSeason(increaseRateComparedToOffSeason)
                .nextSeasonStartDate(nextSeasonStartDate)
                .nextSeasonDDay(nextSeasonDDay)
                .nextSeasonRoomTypeCount(nextSeasonRoomTypeCount)
                .build();
    }

    private BigDecimal calculateAverageRoomPriceForMonth(List<RoomEntity> rooms, List<SeasonRateEntity> seasons, LocalDate targetMonthDate) {
        if (rooms.isEmpty()) {
            return BigDecimal.ZERO;
        }

        LocalDate startOfMonth = targetMonthDate.withDayOfMonth(1);
        LocalDate endOfMonth = targetMonthDate.withDayOfMonth(targetMonthDate.lengthOfMonth());

        BigDecimal totalSum = BigDecimal.ZERO;
        int totalDaysCount = 0;

        // Loop through each day of the month
        for (LocalDate d = startOfMonth; !d.isAfter(endOfMonth); d = d.plusDays(1)) {
            final LocalDate date = d;
            boolean isWeekend = isWeekend(date);

            // Find applicable season for each room type on this day
            Map<RoomType, SeasonRateEntity> activeSeasons = seasons.stream()
                    .filter(s -> !date.isBefore(s.getStartDate()) && !date.isAfter(s.getEndDate()))
                    .collect(Collectors.toMap(
                            SeasonRateEntity::getRoomType,
                            s -> s,
                            (s1, s2) -> s1 // Keep first in case of duplicates (should not happen due to overlap validation)
                    ));

            for (RoomEntity room : rooms) {
                RoomType type = room.getRoomType();
                BigDecimal price = room.getBasePrice();

                if (type != null && activeSeasons.containsKey(type)) {
                    SeasonRateEntity season = activeSeasons.get(type);
                    price = isWeekend ? season.getWeekendPrice() : season.getWeekdayPrice();
                }

                if (price != null) {
                    totalSum = totalSum.add(price);
                    totalDaysCount++;
                }
            }
        }

        if (totalDaysCount == 0) {
            return BigDecimal.ZERO;
        }

        return totalSum.divide(BigDecimal.valueOf(totalDaysCount), 4, RoundingMode.HALF_UP);
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}
