package com.mjc.hotel.hotelstrans;

import com.mjc.hotel.hotelsimage.HotelImageDto;
import com.mjc.hotel.hotelsimage.HotelImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/hotelTrans")
public class HotelTransRestController {
    private final HotelTransService hotelTransService;

    @GetMapping
    public List<HotelImageDto> getHotelImages() {
        return hotelTransService.findAll();
    }
    @GetMapping("/{hotelImageId}")
    public HotelImageDto getHotelImageById(@PathVariable Long hotelImageId) {
        return hotelTransService.findById(hotelImageId);
    }

    @PostMapping
    public HotelImageDto insertHotelImage(@RequestBody HotelImageDto hotelImageDto) {
        return hotelTransService.insert(hotelImageDto);
    }

}
