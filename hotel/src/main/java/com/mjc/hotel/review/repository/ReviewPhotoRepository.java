package com.mjc.hotel.review.repository;

import com.mjc.hotel.review.entity.ReviewPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto, Long> {
    List<ReviewPhoto> findByReviewIdOrderByPhotoOrderAscIdAsc(Long reviewId);

    void deleteByReviewId(Long reviewId);
}
