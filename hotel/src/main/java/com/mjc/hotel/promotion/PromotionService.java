package com.mjc.hotel.promotion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {
    @Autowired
    private PromotionRepository prePromotionRepository;
}
