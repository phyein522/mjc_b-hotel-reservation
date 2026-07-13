package com.mjc.hotel.rates.repository;

import com.mjc.hotel.rates.entity.RatePolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatePolicyRepository extends JpaRepository<RatePolicyEntity, Long> {
    Optional<RatePolicyEntity> findByHotel_HotelId(Long hotelId);
}
