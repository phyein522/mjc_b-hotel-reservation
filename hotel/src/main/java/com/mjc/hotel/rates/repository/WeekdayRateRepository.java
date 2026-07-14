package com.mjc.hotel.rates.repository;

import com.mjc.hotel.rates.entity.WeekdayRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeekdayRateRepository extends JpaRepository<WeekdayRateEntity, Long> {
    List<WeekdayRateEntity> findBySeasonRate_SeasonRateId(Long seasonRateId);
}
