package com.mjc.hotel.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotion")
@RequiredArgsConstructor
public class PromotionRestController {

    private final PromotionService promotionService;

    @GetMapping
    public List<PromotionDto> findAll() {
        return promotionService.findAll();
    }

    @GetMapping("/{proId}")
    public PromotionDto findById(@PathVariable Long proId) {
        return promotionService.findById(proId);
    }

    @PostMapping
    public PromotionDto insert(@RequestBody PromotionDto dto) {
        return promotionService.insert(dto);
    }

    @PutMapping("/{proId}")
    public PromotionDto update(@PathVariable Long proId,
                               @RequestBody PromotionDto dto) {

        return promotionService.update(proId, dto);
    }

    @DeleteMapping("/{proId}")
    public ResponseEntity<Void> delete(@PathVariable Long proId) {

        promotionService.deleteById(proId);

        return ResponseEntity.noContent().build();
    }
}
