package com.mjc.hotel.review.entity;

import com.mjc.hotel.review.enums.ReviewRatingCategory;
import com.mjc.hotel.review.enums.ReviewTagType;
import com.mjc.hotel.review.enums.ReviewTripType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "reviews")
@Comment("리뷰")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    @Comment("리뷰 ID")
    private Long reviewId;

    @Column(name = "reservation_id")
    @Comment("예약 ID")
    private Long reservationId;

    @Column(name = "user_id")
    @Comment("작성자 회원 ID")
    private Long userId;

    @Column(name = "hotel_id")
    @Comment("호텔 ID")
    private Long hotelId;

    @Column(name = "room_id")
    @Comment("객실 ID")
    private Long roomId;

    @Convert(converter = ReviewTripTypeConverter.class)
    @Column(name = "trip_type")
    @Comment("여행 유형")
    private ReviewTripType tripType;

    @Column(name = "view_count", nullable = false)
    @Comment("조회수")
    private Short viewCount;

    @Column(name = "title", length = 200)
    @Comment("리뷰 제목")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    @Comment("리뷰 내용")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성 일시")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @Comment("수정 일시")
    private LocalDateTime updatedAt;

    @Column(name = "overall_rating")
    @Comment("전체 평점")
    private Long overallRating;

    @Convert(converter = ReviewTagSetConverter.class)
    @Column(name = "tags", length = 500)
    @Comment("리뷰 태그 목록")
    private Set<ReviewTagType> tags;

    @Convert(converter = ReviewRatingMapConverter.class)
    @Column(name = "category_ratings", length = 500)
    @Comment("카테고리별 평점")
    private Map<ReviewRatingCategory, Long> categoryRatings;

    @Column(name = "like_count")
    @Comment("좋아요 수")
    private Long likeCount;

    @Column(name = "dislike_count")
    @Comment("싫어요 수")
    private Long dislikeCount;
}
