package com.mjc.hotel.hotelsattr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotelattr")
public class HotelAttrRestController {
    @Autowired
    private HotelAttrService hotelAttrService;
    @GetMapping
    public List<HotelAttrDto> getHotelAttr() {
        return hotelAttrService.findAll();
    }
    @GetMapping("/{hotelAttrId}")
    public HotelAttrDto getHotelAttrById(@PathVariable Long hotelAttrId) {
        return hotelAttrService.findById(hotelAttrId);
    }
    @PostMapping
    public HotelAttrDto insertHotelAttr(@RequestBody HotelAttrDto hotelAttrDto) {
        return hotelAttrService.insert(hotelAttrDto);
    }
    @PutMapping("/{hotelAttrId}")
    public HotelAttrDto updateHotelAttr(@PathVariable Long hotelAttrId,
                                          @RequestBody HotelAttrDto hotelAttrDto) {

        return hotelAttrService.update(hotelAttrId, hotelAttrDto);
    }
    @DeleteMapping("/{hotelAttrId}")
    public ResponseEntity<Void> deleteAttrImage(@PathVariable Long hotelAttrId) {

        hotelAttrService.deleteById(hotelAttrId);

        return ResponseEntity.noContent().build();
    }
}
