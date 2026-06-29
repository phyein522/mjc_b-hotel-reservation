package com.mjc.hotel.user.repository;

import com.mjc.hotel.user.entity.User;
import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByStatus(Status status);

    List<User> findByNameContaining(String name);
}
