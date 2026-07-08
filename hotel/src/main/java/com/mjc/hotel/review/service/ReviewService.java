package com.mjc.hotel.review.service;

import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.hotels.HotelRepository;
import com.mjc.hotel.review.dto.IReview;
import com.mjc.hotel.review.dto.ReviewDto;
import com.mjc.hotel.review.dto.ReviewPhotoDto;
import com.mjc.hotel.review.dto.ReviewPhotoRequest;
import com.mjc.hotel.review.dto.ReviewRatingDto;
import com.mjc.hotel.review.dto.ReviewRatingRequest;
import com.mjc.hotel.review.dto.ReviewRequest;
import com.mjc.hotel.review.dto.ReviewResponse;
import com.mjc.hotel.review.dto.ReviewTagDto;
import com.mjc.hotel.review.dto.ReviewTagRequest;
import com.mjc.hotel.review.entity.Review;
import com.mjc.hotel.review.entity.ReviewPhoto;
import com.mjc.hotel.review.enums.ReviewRatingCategory;
import com.mjc.hotel.review.enums.ReviewTagType;
import com.mjc.hotel.review.exception.ReviewNotFoundException;
import com.mjc.hotel.review.exception.ReviewReferenceNotFoundException;
import com.mjc.hotel.review.repository.ReviewPhotoRepository;
import com.mjc.hotel.review.repository.ReviewRepository;
import com.mjc.hotel.rooms.dto.RoomEntity;
import com.mjc.hotel.rooms.service.RoomRepository;
import com.mjc.hotel.user.entity.User;
import com.mjc.hotel.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewPhotoRepository reviewPhotoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;

    @Transactional
    public ReviewDto insert(IReview reviewDto) {
        validateReferences(reviewDto);
        Review insertEntity = toReview(reviewDto);
        insertEntity.setReviewId(null);
        Review insertedEntity = this.reviewRepository.save(insertEntity);
        savePhotos(insertedEntity.getReviewId(), reviewDto.getPhotos());
        return this.findById(insertedEntity.getReviewId());
    }

    @Transactional(readOnly = true)
    public ReviewDto findById(Long reviewId) {
        Review findEntity = this.findReview(reviewId);
        return this.toDtoWithDetails(findEntity);
    }

    @Transactional
    public ReviewDto update(IReview reviewDto) {
        Review updateEntity = this.findReview(reviewDto.getReviewId());
        ReviewDto mergedDto = this.toDto(updateEntity);
        mergedDto.copyMembers(reviewDto, false);
        validateReferences(mergedDto);
        applyReview(updateEntity, mergedDto);
        Review updatedEntity = this.reviewRepository.save(updateEntity);
        if (reviewDto.getPhotos() != null) {
            this.deletePhotos(updatedEntity.getReviewId());
            this.savePhotos(updatedEntity.getReviewId(), reviewDto.getPhotos());
        }
        return this.findById(updatedEntity.getReviewId());
    }

    @Transactional
    public ReviewDto deleteById(Long reviewId) {
        ReviewDto findDto = this.findById(reviewId);
        this.deletePhotos(reviewId);
        this.reviewRepository.deleteById(reviewId);
        return findDto;
    }

    @Transactional(readOnly = true)
    public Page<ReviewDto> findAllByHotelIdEquals(Long hotelId, Pageable pageable) {
        Page<Review> page = this.reviewRepository.findAllByHotelIdEquals(hotelId, pageable);
        List<ReviewDto> list = page.getContent().stream()
                .map(this::toDtoWithDetails)
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        return this.toResponse(this.insert(toDto(request)));
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviews() {
        return this.reviewRepository.findAll()
                .stream()
                .map(review -> this.toResponse(this.toDtoWithDetails(review)))
                .toList();
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long reviewId) {
        return this.toResponse(this.findById(reviewId));
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request) {
        ReviewDto dto = toDto(request);
        dto.setReviewId(reviewId);
        return this.toResponse(this.update(dto));
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        if (!this.reviewRepository.existsById(reviewId)) {
            throw new ReviewNotFoundException(reviewId);
        }
        this.deletePhotos(reviewId);
        this.reviewRepository.deleteById(reviewId);
    }
    @Transactional(readOnly = true)
    public Page<ReviewDto> search(String keyword, Pageable pageable) {
        Page<Review> page = hasText(keyword)
                ? this.reviewRepository.findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword.trim(), keyword.trim(), pageable)
                : this.reviewRepository.findAll(pageable);
        List<ReviewDto> list = page.getContent()
                .stream()
                .map(this::toDtoWithDetails)
                .toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    private Review findReview(Long reviewId) {
        return this.reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
    }

    private Review toReview(IReview dto) {
        Review review = Review.builder().build();
        applyReview(review, dto);
        return review;
    }

    private void applyReview(Review review, IReview dto) {
        review.setReviewId(dto.getReviewId());
        review.setReservationId(dto.getReservationId());
        review.setUserId(dto.getUserId());
        review.setHotelId(dto.getHotelId());
        review.setRoomId(dto.getRoomId());
        review.setTripType(dto.getTripType());
        review.setViewCount(defaultViewCount(dto.getViewCount()));
        review.setTitle(dto.getTitle());
        review.setContent(dto.getContent());
        review.setOverallRating(dto.getOverallRating());
        review.setTags(toTagSet(dto));
        review.setCategoryRatings(toRatingMap(dto));
        review.setLikeCount(defaultCount(dto.getLikeCount()));
        review.setDislikeCount(defaultCount(dto.getDislikeCount()));
    }

    private ReviewDto toDtoWithDetails(Review review) {
        ReviewDto dto = this.toDto(review);
        User user = findUser(review.getUserId());
        HotelEntity hotel = findHotel(review.getHotelId());
        RoomEntity room = findRoomIfPresent(review.getRoomId());
        dto.setUserName(user.getName());
        dto.setHotelName(hotel.getName());
        dto.setRoomName(room == null ? null : room.getName());
        dto.setPhotos(this.reviewPhotoRepository.findByReviewIdOrderByPhotoOrderAscIdAsc(review.getReviewId())
                .stream()
                .map(ReviewPhotoDto::from)
                .toList());
        return dto;
    }

    private ReviewDto toDto(Review review) {
        ReviewDto dto = ReviewDto.builder()
                .reviewId(review.getReviewId())
                .reservationId(review.getReservationId())
                .userId(review.getUserId())
                .hotelId(review.getHotelId())
                .roomId(review.getRoomId())
                .tripType(review.getTripType())
                .viewCount(review.getViewCount())
                .title(review.getTitle())
                .content(review.getContent())
                .updatedAt(review.getUpdatedAt())
                .overallRating(review.getOverallRating())
                .likeCount(review.getLikeCount())
                .dislikeCount(review.getDislikeCount())
                .tags(toTagDtos(review.getTags()))
                .ratings(toRatingDtos(review.getCategoryRatings()))
                .build();
        dto.setCreatedAt(review.getCreatedAt());
        dto.setModifiedAt(review.getUpdatedAt());
        return dto;
    }

    private ReviewDto toDto(ReviewRequest request) {
        return ReviewDto.builder()
                .reservationId(request.reservationId())
                .userId(request.userId())
                .hotelId(request.hotelId())
                .roomId(request.roomId())
                .tripType(request.tripType())
                .viewCount(request.viewCount())
                .title(request.title())
                .content(request.content())
                .overallRating(request.overallRating())
                .likeCount(request.likeCount())
                .dislikeCount(request.dislikeCount())
                .photos(toPhotoDtos(request.photos()))
                .tags(toTagDtos(request.tags()))
                .ratings(toRatingDtos(request.ratings()))
                .build();
    }

    private ReviewResponse toResponse(ReviewDto dto) {
        Review review = toReview(dto);
        review.setReviewId(dto.getReviewId());
        review.setCreatedAt(dto.getCreatedAt());
        review.setUpdatedAt(dto.getUpdatedAt());
        return ReviewResponse.from(
                review,
                toPhotoEntities(dto.getPhotos()),
                dto.getUserName(),
                dto.getHotelName(),
                dto.getRoomName()
        );
    }

    private void validateReferences(IReview dto) {
        findUser(dto.getUserId());
        findHotel(dto.getHotelId());
        findRoomIfPresent(dto.getRoomId());
    }

    private User findUser(Long userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new ReviewReferenceNotFoundException("User", userId));
    }

    private HotelEntity findHotel(Long hotelId) {
        return this.hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ReviewReferenceNotFoundException("Hotel", hotelId));
    }

    private RoomEntity findRoomIfPresent(Long roomId) {
        if (roomId == null) {
            return null;
        }
        return this.roomRepository.findById(roomId)
                .orElseThrow(() -> new ReviewReferenceNotFoundException("Room", roomId));
    }

    private void savePhotos(Long reviewId, List<ReviewPhotoDto> photos) {
        if (photos == null || photos.isEmpty()) {
            return;
        }
        List<ReviewPhoto> entities = photos.stream()
                .map(photo -> ReviewPhoto.builder()
                        .reviewId(reviewId)
                        .photoPath(photo.getPhotoPath())
                        .photoOrder(photo.getPhotoOrder())
                        .build())
                .toList();
        this.reviewPhotoRepository.saveAll(entities);
    }

    private void deletePhotos(Long reviewId) {
        this.reviewPhotoRepository.deleteByReviewId(reviewId);
    }

    private Set<ReviewTagType> toTagSet(IReview dto) {
        if (dto.getTags() == null || dto.getTags().isEmpty()) {
            return new LinkedHashSet<>();
        }
        return dto.getTags()
                .stream()
                .map(ReviewTagDto::getTag)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Map<ReviewRatingCategory, Long> toRatingMap(IReview dto) {
        Map<ReviewRatingCategory, Long> ratings = new EnumMap<>(ReviewRatingCategory.class);
        if (dto.getRatings() == null || dto.getRatings().isEmpty()) {
            return ratings;
        }
        dto.getRatings().forEach(rating -> ratings.put(rating.getCategory(), rating.getScore()));
        return ratings;
    }

    private List<ReviewPhotoDto> toPhotoDtos(List<ReviewPhotoRequest> photos) {
        if (photos == null) {
            return null;
        }
        return photos.stream()
                .map(photo -> ReviewPhotoDto.builder()
                        .photoPath(photo.photoPath())
                        .photoOrder(photo.photoOrder())
                        .build())
                .toList();
    }

    private List<ReviewTagDto> toTagDtos(List<ReviewTagRequest> tags) {
        if (tags == null) {
            return null;
        }
        return tags.stream()
                .map(tag -> ReviewTagDto.builder().tag(tag.tag()).build())
                .toList();
    }

    private List<ReviewTagDto> toTagDtos(Set<ReviewTagType> tags) {
        if (tags == null) {
            return List.of();
        }
        return tags.stream()
                .map(tag -> ReviewTagDto.builder().tag(tag).build())
                .toList();
    }

    private List<ReviewRatingDto> toRatingDtos(List<ReviewRatingRequest> ratings) {
        if (ratings == null) {
            return null;
        }
        return ratings.stream()
                .map(rating -> ReviewRatingDto.builder()
                        .category(rating.category())
                        .score(rating.score())
                        .build())
                .toList();
    }

    private List<ReviewRatingDto> toRatingDtos(Map<ReviewRatingCategory, Long> ratings) {
        if (ratings == null) {
            return List.of();
        }
        return ratings.entrySet()
                .stream()
                .map(entry -> ReviewRatingDto.builder()
                        .category(entry.getKey())
                        .score(entry.getValue())
                        .build())
                .toList();
    }

    private List<ReviewPhoto> toPhotoEntities(List<ReviewPhotoDto> photos) {
        if (photos == null) {
            return List.of();
        }
        return photos.stream()
                .map(photo -> ReviewPhoto.builder()
                        .id(photo.getId())
                        .reviewId(photo.getReviewId())
                        .photoPath(photo.getPhotoPath())
                        .photoOrder(photo.getPhotoOrder())
                        .build())
                .toList();
    }

    private Short defaultViewCount(Short viewCount) {
        return viewCount == null ? 0 : viewCount;
    }

    private Long defaultCount(Long count) {
        return count == null ? 0L : count;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
