package com.mjc.hotel.user.controller;

import com.mjc.hotel.user.entity.User;
import com.mjc.hotel.user.mapper.UserMapper;
import com.mjc.hotel.user.repository.UserRepository;
import com.mjc.hotel.users.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/users — 전체 조회
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET /api/users/{userId} — 단건 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // GET /api/users/email?email=... — 이메일 조회
    @GetMapping("/email")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    // GET /api/users/role/{role} — 역할별 조회
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    // GET /api/users/status/{status} — 상태별 조회
    @GetMapping("/status/{status}")
    public ResponseEntity<List<UserResponse>> getUsersByStatus(@PathVariable Status status) {
        return ResponseEntity.ok(userService.getUsersByStatus(status));
    }

    // POST /api/users — 생성
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    // PUT /api/users/{userId} — 전체 수정
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    // PATCH /api/users/{userId}/status — 상태 변경
    @PatchMapping("/{userId}/status")
    public ResponseEntity<UserResponse> changeStatus(
            @PathVariable Long userId,
            @RequestParam Status status) {
        return ResponseEntity.ok(userService.changeStatus(userId, status));
    }

    // DELETE /api/users/{userId} — 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
