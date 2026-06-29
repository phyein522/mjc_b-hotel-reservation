package com.mjc.hotel.review.repository;

import com.mjc.hotel.review.entity.ReviewTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewTagRepository extends JpaRepository<ReviewTag, Long> {
    List<ReviewTag> findByReviewId(Long reviewId);

    void deleteByReviewId(Long reviewId);
}
