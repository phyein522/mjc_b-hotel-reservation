package com.mjc.hotel.review.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReviewReferenceNotFoundException extends RuntimeException {

    public ReviewReferenceNotFoundException(String target, Long id) {
        super(target + " not found. id=" + id);
    }
}
