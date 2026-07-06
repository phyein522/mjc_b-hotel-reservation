package com.mjc.hotel.bookings;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingRestController {
    @Autowired private BookingService bookingService;

    @PostMapping("/insert")
    public ResponseEntity<ApiResponse<BookingDto>> insert(@RequestBody BookingDto insertDto) {
        BookingDto result = this.bookingService.insert(insertDto);
        return ResponseEntity.ok().body(
                new ApiResponse<>(ResponseCode.INSERT_OK, "booking insert ok", result)
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<BookingDto>>> findAllByUserId(@PathVariable Long userId) {
        List<BookingDto> result = this.bookingService.findAllByUserId(userId);
        return ResponseEntity.ok().body(
                new ApiResponse<>(ResponseCode.SELECT_OK, "bookings select ok", result)
        );
    }

    @PatchMapping("/cancel/{bookingId}")
    public ResponseEntity<ApiResponse<BookingDto>> cancel(@PathVariable Long bookingId) {
        BookingDto result = this.bookingService.cancel(bookingId);
        return ResponseEntity.ok().body(
                new ApiResponse<>(ResponseCode.UPDATE_OK, "booking cancel ok", result)
        );
    }
}
