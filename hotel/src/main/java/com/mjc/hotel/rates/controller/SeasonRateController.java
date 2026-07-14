package com.mjc.hotel.rates.controller;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.rates.dto.PricePreviewDto;
import com.mjc.hotel.rates.dto.SeasonRateDto;
import com.mjc.hotel.rates.service.SeasonRateService;
import com.mjc.hotel.rooms.enums.RoomType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/rates")
public class SeasonRateController {

    @Autowired
    private SeasonRateService seasonRateService;

    @GetMapping("/hotels/{hotelId}/rooms/{roomType}")
    public ResponseEntity<ApiResponse<List<SeasonRateDto>>> getSeasons(
            @PathVariable Long hotelId,
            @PathVariable String roomType) {

        RoomType roomTypeEnum = parseRoomType(roomType);
        List<SeasonRateDto> result = seasonRateService.getSeasonsByHotelAndRoomType(hotelId, roomTypeEnum);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", result)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SeasonRateDto>> insertSeason(
            @RequestBody SeasonRateDto requestDto,
            @RequestParam(required = false, defaultValue = "20.0") BigDecimal multiplier) {

        SeasonRateDto result = seasonRateService.createSeason(requestDto, multiplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.make(ResponseCode.INSERT_OK, "ok", result)
        );
    }

    @PutMapping("/{seasonRateId}")
    public ResponseEntity<ApiResponse<SeasonRateDto>> updateSeason(
            @PathVariable Long seasonRateId,
            @RequestBody SeasonRateDto requestDto,
            @RequestParam(required = false, defaultValue = "20.0") BigDecimal multiplier) {

        SeasonRateDto result = seasonRateService.updateSeason(seasonRateId, requestDto, multiplier);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.UPDATE_OK, "ok", result)
        );
    }

    @DeleteMapping("/{seasonRateId}")
    public ResponseEntity<ApiResponse<Void>> deleteSeason(@PathVariable Long seasonRateId) {
        seasonRateService.deleteSeason(seasonRateId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.DELETE_OK, "ok", null)
        );
    }

    @PostMapping("/preview")
    public ResponseEntity<ApiResponse<PricePreviewDto>> getPreview(@RequestBody PricePreviewDto previewDto) {
        PricePreviewDto result = seasonRateService.calculatePreview(previewDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", result)
        );
    }

    private RoomType parseRoomType(String roomType) {
        if (roomType == null) {
            throw new IllegalArgumentException("객실 유형(roomType)은 필수입니다.");
        }
        String clean = roomType.trim().toLowerCase();
        if (clean.equals("1") || clean.contains("standard")) {
            return RoomType.Standard;
        } else if (clean.equals("2") || clean.contains("suite")) {
            return RoomType.Suite;
        } else if (clean.equals("3") || clean.contains("deluxe")) {
            return RoomType.Deluxe;
        } else if (clean.equals("4") || clean.contains("premium")) {
            return RoomType.Premium;
        } else {
            throw new IllegalArgumentException("지원하지 않는 객실 유형입니다: " + roomType);
        }
    }
}
