package com.mjc.hotel.promotionSale;

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
@RequestMapping("/api/promotionsale")
public class PromotionSaleRestController {

    @Autowired
    private PromotionSaleService promotionSaleService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PromotionSaleDto>>> page(
            @PageableDefault(size = 10, page = 0, sort = "proSaleId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PromotionSaleDto> page = this.promotionSaleService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", page)
        );
    }

    @GetMapping("/{proSaleId}")
    public ResponseEntity<ApiResponse<PromotionSaleDto>> findById(@PathVariable Long proSaleId) {
        PromotionSaleDto resultDto = this.promotionSaleService.findById(proSaleId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", resultDto)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PromotionSaleDto>> insert(@RequestBody PromotionSaleDto requestDto) {
        PromotionSaleDto resultDto = this.promotionSaleService.insert(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
        );
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<PromotionSaleDto>> update(@RequestBody PromotionSaleDto requestDto) {
        PromotionSaleDto resultDto = this.promotionSaleService.update(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.UPDATE_OK, "ok", resultDto)
        );
    }

    @DeleteMapping("/{proSaleId}")
    public ResponseEntity<ApiResponse<PromotionSaleDto>> deleteById(
            @PathVariable Long proSaleId,
            @RequestParam Long userId) {
        PromotionSaleDto resultDto = this.promotionSaleService.deleteById(proSaleId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.DELETE_OK, "ok", resultDto)
        );
    }

    @GetMapping("/promotion/{proId}")
    public ResponseEntity<ApiResponse<Page<PromotionSaleDto>>> pageByProId(
            @PathVariable Long proId,
            @PageableDefault(size = 10, page = 0, sort = "proSaleId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PromotionSaleDto> page = this.promotionSaleService.findAllByProIdEquals(proId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", page)
        );
    }
}
