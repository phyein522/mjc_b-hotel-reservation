package com.mjc.hotel.hotelsimage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/hotelimages")
public class HotelImageRestController {
    private final HotelImageService hotelImageService;

    @GetMapping
    public List<HotelImageDto> getHotelImages() {
        return hotelImageService.findAll();
    }
    @GetMapping("/{hotelImageId}")
    public HotelImageDto getHotelImageById(@PathVariable Long hotelImageId) {
        return hotelImageService.findById(hotelImageId);
    }

    @PostMapping
    public HotelImageDto insertHotelImage(@RequestBody HotelImageDto hotelImageDto) {
        return hotelImageService.insert(hotelImageDto);
    }

    @PutMapping("/{hotelImageId}")
    public HotelImageDto updateHotelImage(@PathVariable Long hotelImageId,
                                          @RequestBody HotelImageDto hotelImageDto) {

        return hotelImageService.update(hotelImageId, hotelImageDto);
    }

    @DeleteMapping("/{hotelImageId}")
    public ResponseEntity<Void> deleteHotelImage(@PathVariable Long hotelImageId) {

        hotelImageService.deleteById(hotelImageId);

        return ResponseEntity.noContent().build();
    }
}
