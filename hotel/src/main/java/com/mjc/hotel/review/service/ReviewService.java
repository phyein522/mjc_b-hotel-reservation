package com.mjc.hotel.review.service;

import com.mjc.hotel.review.dto.ReviewPhotoRequest;
import com.mjc.hotel.review.dto.ReviewRatingRequest;
import com.mjc.hotel.review.dto.ReviewRequest;
import com.mjc.hotel.review.dto.ReviewResponse;
import com.mjc.hotel.review.dto.ReviewTagRequest;
import com.mjc.hotel.review.entity.Review;
import com.mjc.hotel.review.entity.ReviewPhoto;
import com.mjc.hotel.review.entity.ReviewRating;
import com.mjc.hotel.review.entity.ReviewTag;
import com.mjc.hotel.review.exception.ReviewNotFoundException;
import com.mjc.hotel.review.repository.ReviewPhotoRepository;
import com.mjc.hotel.review.repository.ReviewRatingRepository;
import com.mjc.hotel.review.repository.ReviewRepository;
import com.mjc.hotel.review.repository.ReviewTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final ReviewTagRepository reviewTagRepository;
    private final ReviewRatingRepository reviewRatingRepository;

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
        review.setTripTypeSelectionId(request.tripTypeSelectionId());
        review.setViewCount(defaultViewCount(request.viewCount()));
        review.setTitle(request.title());
        review.setContent(request.content());
        review.setOverallRating(request.overallRating());
        review.setLikeCount(defaultCount(request.likeCount()));
        review.setDislikeCount(defaultCount(request.dislikeCount()));

        deleteDetails(reviewId);
        saveDetails(reviewId, request);

        return getReview(reviewId);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ReviewNotFoundException(reviewId);
        }

        deleteDetails(reviewId);
        reviewRepository.deleteById(reviewId);
    }

    private Review findReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
    }

    private ReviewResponse toResponse(Long reviewId, Review review) {
        return ReviewResponse.from(
                review,
                reviewPhotoRepository.findByReviewIdOrderByPhotoOrderAscIdAsc(reviewId),
                reviewTagRepository.findByReviewId(reviewId),
                reviewRatingRepository.findByReviewId(reviewId)
        );
    }

    private Review toReview(ReviewRequest request) {
        return Review.builder()
                .reservationId(request.reservationId())
                .userId(request.userId())
                .hotelId(request.hotelId())
                .tripTypeSelectionId(request.tripTypeSelectionId())
                .viewCount(defaultViewCount(request.viewCount()))
                .title(request.title())
                .content(request.content())
                .overallRating(request.overallRating())
                .likeCount(defaultCount(request.likeCount()))
                .dislikeCount(defaultCount(request.dislikeCount()))
                .build();
    }

    private void saveDetails(Long reviewId, ReviewRequest request) {
        savePhotos(reviewId, request.photos());
        saveTags(reviewId, request.tags());
        saveRatings(reviewId, request.ratings());
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

    private void saveTags(Long reviewId, List<ReviewTagRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        List<ReviewTag> tags = requests.stream()
                .map(tag -> ReviewTag.builder()
                        .reviewId(reviewId)
                        .tagId(tag.tagId())
                        .status(tag.status())
                        .build())
                .toList();

        reviewTagRepository.saveAll(tags);
    }

    private void saveRatings(Long reviewId, List<ReviewRatingRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        List<ReviewRating> ratings = requests.stream()
                .map(rating -> ReviewRating.builder()
                        .reviewId(reviewId)
                        .categoryId(rating.categoryId())
                        .score(rating.score())
                        .build())
                .toList();

        reviewRatingRepository.saveAll(ratings);
    }

    private void deleteDetails(Long reviewId) {
        reviewPhotoRepository.deleteByReviewId(reviewId);
        reviewTagRepository.deleteByReviewId(reviewId);
        reviewRatingRepository.deleteByReviewId(reviewId);
    }

    private Short defaultViewCount(Short viewCount) {
        return viewCount == null ? 0 : viewCount;
    }

    private Long defaultCount(Long count) {
        return count == null ? 0L : count;
    }
}
