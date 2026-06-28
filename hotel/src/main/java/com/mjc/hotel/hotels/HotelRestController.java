package com.mjc.hotel.hotels;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hotels")
public class HotelRestController {
    private final HotelService hotelService;

    @GetMapping
    public List<HotelDto> getHotels() {
        return hotelService.findAll();
    }

    @GetMapping("/{hotelId}")
    public HotelDto getHotel(@PathVariable Long hotelId) {
        return hotelService.findById(hotelId);
    }

    @PostMapping
    public HotelDto insertHotel(@RequestBody HotelDto insertDto) {
        return hotelService.insert(insertDto);
    }

    @PutMapping("/{hotelId}")
    public HotelDto updateHotel(@PathVariable Long hotelId,
                                @RequestBody HotelDto hotelDto) {
        return hotelService.update(hotelId, hotelDto);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long hotelId) {
        hotelService.deleteByID(hotelId);
        return ResponseEntity.noContent().build();
    }
}
