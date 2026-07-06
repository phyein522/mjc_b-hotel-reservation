package com.mjc.hotel.review;

import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.hotels.HotelRepository;
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
import com.mjc.hotel.review.exception.ReviewReferenceNotFoundException;
import com.mjc.hotel.review.repository.ReviewPhotoRepository;
import com.mjc.hotel.review.repository.ReviewRepository;
import com.mjc.hotel.review.service.ReviewService;
import com.mjc.hotel.rooms.dto.RoomEntity;
import com.mjc.hotel.rooms.service.RoomRepository;
import com.mjc.hotel.user.entity.User;
import com.mjc.hotel.user.repository.UserRepository;
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

    @Mock
    private UserRepository userRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void createReviewSavesReviewWithLinkedUserHotelRoom() {
        Review savedReview = savedReview(10L);
        ReviewPhoto photo = ReviewPhoto.builder().id(1L).reviewId(10L).photoPath("/a.jpg").photoOrder(1L).build();
        stubReferences();

        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(savedReview));
        when(reviewPhotoRepository.findByReviewIdOrderByPhotoOrderAscIdAsc(10L)).thenReturn(List.of(photo));

        ReviewResponse response = reviewService.createReview(request());

        assertThat(response.reviewId()).isEqualTo(10L);
        assertThat(response.userName()).isEqualTo("Alice");
        assertThat(response.hotelName()).isEqualTo("Ocean Hotel");
        assertThat(response.roomId()).isEqualTo(4L);
        assertThat(response.roomName()).isEqualTo("Deluxe 401");
        assertThat(response.tripType()).isEqualTo(ReviewTripType.FAMILY);
        assertThat(response.photos()).hasSize(1);
        assertThat(response.tags()).extracting("tag").containsExactly(ReviewTagType.CLEAN);
        assertThat(response.ratings()).hasSize(1);
        assertThat(response.ratings().getFirst().category()).isEqualTo(ReviewRatingCategory.CLEANLINESS);
        verify(reviewPhotoRepository).saveAll(any());
    }

    @Test
    void createReviewRejectsMissingUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(request()))
                .isInstanceOf(ReviewReferenceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void updateReviewReplacesPhotosAndStoresLinkedIds() {
        Review review = savedReview(10L);
        stubReferences();

        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewPhotoRepository.findByReviewIdOrderByPhotoOrderAscIdAsc(10L)).thenReturn(List.of());

        ReviewResponse response = reviewService.updateReview(10L, request());

        assertThat(response.title()).isEqualTo("Good stay");
        assertThat(review.getUserId()).isEqualTo(2L);
        assertThat(review.getHotelId()).isEqualTo(3L);
        assertThat(review.getRoomId()).isEqualTo(4L);
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
        stubReferences();
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review review = invocation.getArgument(0);
            review.setReviewId(10L);
            return review;
        });
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(savedReview(10L)));
        when(reviewPhotoRepository.findByReviewIdOrderByPhotoOrderAscIdAsc(10L)).thenReturn(List.of());

        reviewService.createReview(new ReviewRequest(
                1L, 2L, 3L, 4L, ReviewTripType.SOLO, null, "Title", "Content",
                5L, null, null, List.of(), List.of(), List.of()
        ));

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(captor.capture());
        assertThat(captor.getValue().getViewCount()).isZero();
        assertThat(captor.getValue().getLikeCount()).isZero();
        assertThat(captor.getValue().getDislikeCount()).isZero();
    }

    private void stubReferences() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user()));
        when(hotelRepository.findById(3L)).thenReturn(Optional.of(hotel()));
        when(roomRepository.findById(4L)).thenReturn(Optional.of(room()));
    }

    private ReviewRequest request() {
        return new ReviewRequest(
                1L,
                2L,
                3L,
                4L,
                ReviewTripType.FAMILY,
                (short) 0,
                "Good stay",
                "Clean room and kind staff.",
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
                .roomId(4L)
                .tripType(ReviewTripType.FAMILY)
                .viewCount((short) 0)
                .title("Good stay")
                .content("Clean room and kind staff.")
                .overallRating(5L)
                .tags(new LinkedHashSet<>(Set.of(ReviewTagType.CLEAN)))
                .categoryRatings(ratings)
                .likeCount(0L)
                .dislikeCount(0L)
                .build();
    }

    private User user() {
        return User.builder()
                .userId(2L)
                .name("Alice")
                .email("alice@example.com")
                .password("password")
                .build();
    }

    private HotelEntity hotel() {
        return HotelEntity.builder()
                .hotelId(3L)
                .name("Ocean Hotel")
                .build();
    }

    private RoomEntity room() {
        return RoomEntity.builder()
                .roomId(4L)
                .name("Deluxe 401")
                .build();
    }
}
