package com.mjc.hotel.user.controller;

import com.mjc.hotel.user.dto.UserRequest;
import com.mjc.hotel.user.dto.UserResponse;
import com.mjc.hotel.user.entity.User;

import com.mjc.hotel.user.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController {


    private final UserRepository userRepository;


    // ===== JPA 기반 =====

    @GetMapping("/repositoryUsers")
    public List<UserResponse> getUsersByRepository() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/repositoryUsers/{userId}")
    public ResponseEntity<UserResponse> getUserByRepository(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(UserResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/repositoryUsers")
    public ResponseEntity<UserResponse> createUserByRepository(@Valid @RequestBody UserRequest request) {
        // 평문 저장 (개발용, 보안 없음)
        User saved = userRepository.save(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(saved));
    }

    @PutMapping("/repositoryUsers/{userId}")
    public ResponseEntity<UserResponse> updateUserByRepository(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequest request) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        User user = request.toEntity();
        user.setUserId(userId);
        User saved = userRepository.save(user);
        return ResponseEntity.ok(UserResponse.from(saved));
    }

    @DeleteMapping("/repositoryUsers/{userId}")
    public ResponseEntity<Void> deleteUserByRepository(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(userId);
        return ResponseEntity.noContent().build();
    }
}