package com.mjc.hotel.rates.repository;

import com.mjc.hotel.rates.entity.SeasonRateEntity;
import com.mjc.hotel.rooms.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeasonRateRepository extends JpaRepository<SeasonRateEntity, Long> {
    List<SeasonRateEntity> findByHotel_HotelIdAndRoomType(Long hotelId, RoomType roomType);
    List<SeasonRateEntity> findByHotel_HotelId(Long hotelId);
}
