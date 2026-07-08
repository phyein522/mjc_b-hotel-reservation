package com.mjc.hotel.review.controller;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.review.dto.ReviewDto;
import com.mjc.hotel.review.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/review")
public class ReviewRestController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDto>> insert(@RequestBody ReviewDto requestDto) {
        ReviewDto resultDto = this.reviewService.insert(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.make(ResponseCode.INSERT_OK, "ok", resultDto)
        );
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<ReviewDto>> update(@RequestBody ReviewDto requestDto) {
        ReviewDto resultDto = this.reviewService.update(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.UPDATE_OK, "ok", resultDto)
        );
    }
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReviewDto>>> search(
            @RequestParam(name = "keyword", required = false) String keyword,
            @PageableDefault(size = 10, page = 0, sort = "reviewId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReviewDto> page = this.reviewService.search(keyword, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", page)
        );
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDto>> findById(@PathVariable Long reviewId) {
        ReviewDto resultDto = this.reviewService.findById(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", resultDto)
        );
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDto>> deleteById(@PathVariable Long reviewId) {
        ReviewDto resultDto = this.reviewService.deleteById(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.DELETE_OK, "ok", resultDto)
        );
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<ApiResponse<Page<ReviewDto>>> page(@PathVariable Long hotelId,
            @PageableDefault(size = 10, page = 0, sort = "reviewId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReviewDto> page = this.reviewService.findAllByHotelIdEquals(hotelId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.make(ResponseCode.SELECT_OK, "ok", page)
        );
    }
}
