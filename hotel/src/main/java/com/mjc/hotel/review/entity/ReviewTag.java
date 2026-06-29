package com.mjc.hotel.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews_tags")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReviewTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_tag_id")
    private Long reviewTagId;

    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "status")
    private Boolean status;
}
