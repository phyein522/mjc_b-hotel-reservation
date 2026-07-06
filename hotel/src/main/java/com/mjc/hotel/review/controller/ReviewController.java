package com.mjc.hotel.review.controller;

import com.mjc.hotel.review.dto.ReviewRequest;
import com.mjc.hotel.review.dto.ReviewResponse;
import com.mjc.hotel.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ReviewResponse createReview(@RequestBody ReviewRequest request) {
        return reviewService.createReview(request);
    }

    @GetMapping
    public List<ReviewResponse> getReviews() {
        return reviewService.getReviews();
    }

    @GetMapping("/{reviewId}")
    public ReviewResponse getReview(@PathVariable Long reviewId) {
        return reviewService.getReview(reviewId);
    }

    @PutMapping("/{reviewId}")
    public ReviewResponse updateReview(@PathVariable Long reviewId, @RequestBody ReviewRequest request) {
        return reviewService.updateReview(reviewId, request);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
    }
}
