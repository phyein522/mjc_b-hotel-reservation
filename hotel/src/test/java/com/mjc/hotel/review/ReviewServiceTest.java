package com.mjc.hotel.review;

import com.mjc.hotel.review.dto.ReviewPhotoRequest;
import com.mjc.hotel.review.dto.ReviewRatingRequest;
import com.mjc.hotel.review.dto.ReviewRequest;
import com.mjc.hotel.review.dto.ReviewResponse;
import com.mjc.hotel.review.dto.ReviewTagRequest;
import com.mjc.hotel.review.entity.Review;
import com.mjc.hotel.review.entity.ReviewPhoto;
import com.mjc.hotel.review.enums.ReviewRatingCategory;
import com.mjc.hotel.review.enums.ReviewTagType;
import com.mjc.hotel.review.enums.ReviewTripType;
import com.mjc.hotel.review.exception.ReviewNotFoundException;
import com.mjc.hotel.review.repository.ReviewPhotoRepository;
import com.mjc.hotel.review.repository.ReviewRepository;
import com.mjc.hotel.review.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewPhotoRepository reviewPhotoRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void createReviewSavesReviewWithPhotosAndEnumDetails() {
        Review savedReview = savedReview(10L);
        ReviewPhoto photo = ReviewPhoto.builder().id(1L).reviewId(10L).photoPath("/a.jpg").photoOrder(1L).build();

        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(savedReview));
        when(reviewPhotoRepository.findByReviewIdOrderByPhotoOrderAscIdAsc(10L)).thenReturn(List.of(photo));

        ReviewResponse response = reviewService.createReview(request());

        assertThat(response.reviewId()).isEqualTo(10L);
        assertThat(response.tripType()).isEqualTo(ReviewTripType.FAMILY);
        assertThat(response.photos()).hasSize(1);
        assertThat(response.tags()).extracting("tag").containsExactly(ReviewTagType.CLEAN);
        assertThat(response.ratings()).hasSize(1);
        assertThat(response.ratings().getFirst().category()).isEqualTo(ReviewRatingCategory.CLEANLINESS);
        verify(reviewPhotoRepository).saveAll(any());
    }

    @Test
    void updateReviewReplacesPhotosAndStoresEnumDetailsOnReview() {
        Review review = savedReview(10L);
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));
        when(reviewPhotoRepository.findByReviewIdOrderByPhotoOrderAscIdAsc(10L)).thenReturn(List.of());

        ReviewResponse response = reviewService.updateReview(10L, request());

        assertThat(response.title()).isEqualTo("좋은 숙소");
        assertThat(review.getTags()).containsExactly(ReviewTagType.CLEAN);
        assertThat(review.getCategoryRatings()).containsEntry(ReviewRatingCategory.CLEANLINESS, 5L);
        verify(reviewPhotoRepository).deleteByReviewId(10L);
        verify(reviewPhotoRepository).saveAll(any());
    }

    @Test
    void deleteReviewRemovesPhotosBeforeReview() {
        when(reviewRepository.existsById(10L)).thenReturn(true);

        reviewService.deleteReview(10L);

        verify(reviewPhotoRepository).deleteByReviewId(10L);
        verify(reviewRepository).deleteById(10L);
    }

    @Test
    void getReviewThrowsReviewNotFoundExceptionWhenMissing() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.getReview(99L))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessageContaining("reviewId=99");
    }

    @Test
    void createReviewDefaultsEmptyCounters() {
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review review = invocation.getArgument(0);
            review.setReviewId(10L);
            return review;
        });
        when(reviewRepository.findById(10L)).thenAnswer(invocation -> Optional.of(captureSavedReview()));
        when(reviewPhotoRepository.findByReviewIdOrderByPhotoOrderAscIdAsc(10L)).thenReturn(List.of());

        reviewService.createReview(new ReviewRequest(
                1L, 2L, 3L, ReviewTripType.SOLO, null, "제목", "내용",
                5L, null, null, List.of(), List.of(), List.of()
        ));

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(captor.capture());
        assertThat(captor.getValue().getViewCount()).isZero();
        assertThat(captor.getValue().getLikeCount()).isZero();
        assertThat(captor.getValue().getDislikeCount()).isZero();
    }

    private ReviewRequest request() {
        return new ReviewRequest(
                1L,
                2L,
                3L,
                ReviewTripType.FAMILY,
                (short) 0,
                "좋은 숙소",
                "깨끗하고 편했습니다.",
                5L,
                0L,
                0L,
                List.of(new ReviewPhotoRequest("/a.jpg", 1L)),
                List.of(new ReviewTagRequest(ReviewTagType.CLEAN)),
                List.of(new ReviewRatingRequest(ReviewRatingCategory.CLEANLINESS, 5L))
        );
    }

    private Review savedReview(Long reviewId) {
        Map<ReviewRatingCategory, Long> ratings = new EnumMap<>(ReviewRatingCategory.class);
        ratings.put(ReviewRatingCategory.CLEANLINESS, 5L);

        return Review.builder()
                .reviewId(reviewId)
                .reservationId(1L)
                .userId(2L)
                .hotelId(3L)
                .tripType(ReviewTripType.FAMILY)
                .viewCount((short) 0)
                .title("좋은 숙소")
                .content("깨끗하고 편했습니다.")
                .overallRating(5L)
                .tags(new LinkedHashSet<>(Set.of(ReviewTagType.CLEAN)))
                .categoryRatings(ratings)
                .likeCount(0L)
                .dislikeCount(0L)
                .build();
    }

    private Review captureSavedReview() {
        return Review.builder()
                .reviewId(10L)
                .viewCount((short) 0)
                .tags(new LinkedHashSet<>())
                .categoryRatings(new EnumMap<>(ReviewRatingCategory.class))
                .likeCount(0L)
                .dislikeCount(0L)
                .build();
    }
}
