package com.mjc.hotel.hotelsamen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotelamenities")
public class HotelAmenRestController {
    @Autowired
    private HotelAmenService hotelAmenService;
    @GetMapping
    public List<HotelAmenDto> getHotelAmen() {
        return hotelAmenService.findAll();
    }
    @GetMapping("/{hotelAmenId}")
    public HotelAmenDto getHotelAmenById(@PathVariable Long hotelAmenId) {
        return hotelAmenService.findById(hotelAmenId);
    }
    @PostMapping
    public HotelAmenDto insertHotelAmen(@RequestBody HotelAmenDto hotelAmenDto) {
        return hotelAmenService.insert(hotelAmenDto);
    }
    @PutMapping("/{hotelAmenId}")
    public HotelAmenDto updateHotelAmen(@PathVariable Long hotelAmenId,
                                        @RequestBody HotelAmenDto hotelAmenDto) {

        return hotelAmenService.update(hotelAmenId, hotelAmenDto);
    }
    @DeleteMapping("/{hotelAmenId}")
    public ResponseEntity<Void> deleteAmenImage(@PathVariable Long hotelAmenId) {

        hotelAmenService.deleteById(hotelAmenId);

        return ResponseEntity.noContent().build();
    }
}
