package com.mjc.hotel.hotelstrans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hoteltrans")
public class HotelTransRestController {
    @Autowired
    private HotelTransService hotelTransService;
    @GetMapping
    public List<HotelTransDto> getTrans() {
        return hotelTransService.findAll();
    }
    @GetMapping("/{transId}")
    public HotelTransDto getTransById(@PathVariable Long transId) {
        return hotelTransService.findById(transId);
    }

    @PostMapping
    public HotelTransDto insertTrans(@RequestBody HotelTransDto hotelTransDto) {
        return hotelTransService.insert(hotelTransDto);
    }
    @PutMapping("/{transId}")
    public HotelTransDto updateTrans(@PathVariable Long transId,
                                          @RequestBody HotelTransDto hotelTransDto) {

        return hotelTransService.update(transId, hotelTransDto);
    }
    @DeleteMapping("/{transId}")
    public ResponseEntity<Void> deleteTrans(@PathVariable Long transId) {

        hotelTransService.deleteById(transId);

        return ResponseEntity.noContent().build();
    }

}
