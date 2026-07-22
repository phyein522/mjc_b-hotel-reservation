package com.mjc.hotel.promotion;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotion")
public class PromotionRestController {

    @Autowired
    private PromotionService promotionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PromotionDto>>> page(
            @PageableDefault(size = 10, page = 0, sort = "proId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PromotionDto> page = this.promotionService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", page)
        );
    }

    @GetMapping("/{proId}")
    public ResponseEntity<ApiResponse<PromotionDto>> findById(@PathVariable Long proId) {
        PromotionDto resultDto = this.promotionService.findById(proId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", resultDto)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PromotionDto>> insert(@RequestBody PromotionDto requestDto) {
        PromotionDto resultDto = this.promotionService.insert(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
        );
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<PromotionDto>> update(@RequestBody PromotionDto requestDto) {
        PromotionDto resultDto = this.promotionService.update(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.UPDATE_OK, "ok", resultDto)
        );
    }

    @DeleteMapping("/{proId}")
    public ResponseEntity<ApiResponse<PromotionDto>> deleteById(
            @PathVariable Long proId,
            @RequestParam Long userId) {
        PromotionDto resultDto = this.promotionService.deleteById(proId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.DELETE_OK, "ok", resultDto)
        );
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiResponse<Page<PromotionDto>>> pageByRoomId(
            @PathVariable Long roomId,
            @PageableDefault(size = 10, page = 0, sort = "proId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PromotionDto> page = this.promotionService.findAllByRoomIdEquals(roomId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", page)
        );
    }
}
