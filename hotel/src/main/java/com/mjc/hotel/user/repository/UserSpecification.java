package com.mjc.hotel.user.repository;

import com.mjc.hotel.user.dto.UserDto;
import com.mjc.hotel.user.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    // UserDto의 name, email, role, status, membership만 검색 조건으로 사용
    public static Specification<User> buildSpec(UserDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null && !filter.getName().isBlank())
                predicates.add(cb.like(root.get("name"), "%" + filter.getName() + "%"));

            if (filter.getEmail() != null && !filter.getEmail().isBlank())
                predicates.add(cb.like(root.get("email"), "%" + filter.getEmail() + "%"));

            if (filter.getRole() != null)
                predicates.add(cb.equal(root.get("role"), filter.getRole()));

            if (filter.getStatus() != null)
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));

            if (filter.getMembership() != null)
                predicates.add(cb.equal(root.get("membership"), filter.getMembership()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}