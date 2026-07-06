package com.mjc.hotel.user.repository;

import com.mjc.hotel.user.dto.UserSearchRequest;
import com.mjc.hotel.user.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> buildSpec(UserSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getName() != null && !request.getName().isBlank()) {
                predicates.add(cb.like(root.get("name"), "%" + request.getName() + "%"));
            }
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                predicates.add(cb.like(root.get("email"), "%" + request.getEmail() + "%"));
            }
            if (request.getRole() != null) {
                predicates.add(cb.equal(root.get("role"), request.getRole()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getMembership() != null) {
                predicates.add(cb.equal(root.get("membership"), request.getMembership()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}