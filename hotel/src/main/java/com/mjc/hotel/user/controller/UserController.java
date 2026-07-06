package com.mjc.hotel.user.controller;

import com.mjc.hotel.user.dto.*;
import com.mjc.hotel.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    // GET /api/users/check-email?email= — 이메일 중복 체크
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(Map.of("available", userService.checkEmailAvailable(email)));
    }

    // GET /api/users/search — 페이징 + 복합 검색
    // 예: /api/users/search?name=홍&status=ACTIVE&page=0&size=10&sortBy=createdAt&sortDir=desc
    @GetMapping("/search")
    public ResponseEntity<Page<UserResponse>> searchUsers(UserSearchRequest request) {
        return ResponseEntity.ok(userService.searchUsers(request));
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

    // PATCH /api/users/{userId} — 부분 수정 (이름/전화번호/상태/멤버십)
    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> patchUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserPatchRequest request) {
        return ResponseEntity.ok(userService.patchUser(userId, request));
    }

    // PATCH /api/users/{userId}/password — 비밀번호 변경
    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/users/{userId}/withdraw — 소프트 딜리트 (탈퇴)
    @PatchMapping("/{userId}/withdraw")
    public ResponseEntity<Void> withdrawUser(@PathVariable Long userId) {
        userService.withdrawUser(userId);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/users/{userId} — 하드 딜리트
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}