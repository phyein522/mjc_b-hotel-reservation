package com.mjc.hotel.promotion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/promotion")
public class PromotionRestController {
    @Autowired
    private PromotionService promotionService;
}
