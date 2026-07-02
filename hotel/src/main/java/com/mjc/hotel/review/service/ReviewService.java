package com.mjc.hotel.review.service;

import com.mjc.hotel.review.dto.ReviewPhotoRequest;
import com.mjc.hotel.review.dto.ReviewRequest;
import com.mjc.hotel.review.dto.ReviewResponse;
import com.mjc.hotel.review.entity.ReviewRatingCategory;
import com.mjc.hotel.review.entity.Review;
import com.mjc.hotel.review.entity.ReviewPhoto;
import com.mjc.hotel.review.entity.ReviewTagType;
import com.mjc.hotel.review.exception.ReviewNotFoundException;
import com.mjc.hotel.review.repository.ReviewPhotoRepository;
import com.mjc.hotel.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        Review review = reviewRepository.save(toReview(request));
        saveDetails(review.getReviewId(), request);
        return getReview(review.getReviewId());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(review -> toResponse(review.getReviewId(), review))
                .toList();
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long reviewId) {
        Review review = findReview(reviewId);
        return toResponse(reviewId, review);
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request) {
        Review review = findReview(reviewId);

        review.setReservationId(request.reservationId());
        review.setUserId(request.userId());
        review.setHotelId(request.hotelId());
        review.setTripType(request.tripType());
        review.setViewCount(defaultViewCount(request.viewCount()));
        review.setTitle(request.title());
        review.setContent(request.content());
        review.setOverallRating(request.overallRating());
        review.setTags(toTagSet(request));
        review.setCategoryRatings(toRatingMap(request));
        review.setLikeCount(defaultCount(request.likeCount()));
        review.setDislikeCount(defaultCount(request.dislikeCount()));

        deletePhotos(reviewId);
        saveDetails(reviewId, request);

        return getReview(reviewId);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ReviewNotFoundException(reviewId);
        }

        deletePhotos(reviewId);
        reviewRepository.deleteById(reviewId);
    }

    private Review findReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
    }

    private ReviewResponse toResponse(Long reviewId, Review review) {
        return ReviewResponse.from(
                review,
                reviewPhotoRepository.findByReviewIdOrderByPhotoOrderAscIdAsc(reviewId)
        );
    }

    private Review toReview(ReviewRequest request) {
        return Review.builder()
                .reservationId(request.reservationId())
                .userId(request.userId())
                .hotelId(request.hotelId())
                .tripType(request.tripType())
                .viewCount(defaultViewCount(request.viewCount()))
                .title(request.title())
                .content(request.content())
                .overallRating(request.overallRating())
                .tags(toTagSet(request))
                .categoryRatings(toRatingMap(request))
                .likeCount(defaultCount(request.likeCount()))
                .dislikeCount(defaultCount(request.dislikeCount()))
                .build();
    }

    private void saveDetails(Long reviewId, ReviewRequest request) {
        savePhotos(reviewId, request.photos());
    }

    private void savePhotos(Long reviewId, List<ReviewPhotoRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        List<ReviewPhoto> photos = requests.stream()
                .map(photo -> ReviewPhoto.builder()
                        .reviewId(reviewId)
                        .photoPath(photo.photoPath())
                        .photoOrder(photo.photoOrder())
                        .build())
                .toList();

        reviewPhotoRepository.saveAll(photos);
    }

    private void deletePhotos(Long reviewId) {
        reviewPhotoRepository.deleteByReviewId(reviewId);
    }

    private Set<ReviewTagType> toTagSet(ReviewRequest request) {
        if (request.tags() == null || request.tags().isEmpty()) {
            return new LinkedHashSet<>();
        }

        return request.tags()
                .stream()
                .map(tag -> tag.tag())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Map<ReviewRatingCategory, Long> toRatingMap(ReviewRequest request) {
        Map<ReviewRatingCategory, Long> ratings = new EnumMap<>(ReviewRatingCategory.class);
        if (request.ratings() == null || request.ratings().isEmpty()) {
            return ratings;
        }

        request.ratings()
                .forEach(rating -> ratings.put(rating.category(), rating.score()));

        return ratings;
    }

    private Short defaultViewCount(Short viewCount) {
        return viewCount == null ? 0 : viewCount;
    }

    private Long defaultCount(Long count) {
        return count == null ? 0L : count;
    }
}
