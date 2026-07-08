package com.mjc.hotel.promotionSale;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotionsale")
@RequiredArgsConstructor
public class PromotionSaleRestController {

    private final PromotionSaleService promotionSaleService;

    @GetMapping
    public List<PromotionSaleDto> findAll() {
        return promotionSaleService.findAll();
    }

    @GetMapping("/{proSaleId}")
    public PromotionSaleDto findById(@PathVariable Long proSaleId) {
        return promotionSaleService.findById(proSaleId);
    }

    @PostMapping
    public PromotionSaleDto insert(@RequestBody PromotionSaleDto dto) {
        return promotionSaleService.insert(dto);
    }

    @PutMapping("/{proSaleId}")
    public PromotionSaleDto update(@PathVariable Long proSaleId,
                                   @RequestBody PromotionSaleDto dto) {

        return promotionSaleService.update(proSaleId, dto);
    }

    @DeleteMapping("/{proSaleId}")
    public ResponseEntity<Void> delete(@PathVariable Long proSaleId) {

        promotionSaleService.deleteById(proSaleId);

        return ResponseEntity.noContent().build();
    }
}