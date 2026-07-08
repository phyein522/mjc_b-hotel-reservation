package com.mjc.hotel.user.controller;

import com.mjc.hotel.user.dto.PasswordChangeRequest;
import com.mjc.hotel.user.dto.UserDto;
import com.mjc.hotel.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    // 1. 전체 조회: GET /api/users
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 2. 단건 조회: GET /api/users/{userId}  (어색했던 /insert 제거)
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // 3. 이메일 중복 확인: GET /api/users/check-email?email=
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(Map.of("available", userService.checkEmailAvailable(email)));
    }

    // 4. 회원 검색 및 페이징: GET /api/users/search
    @GetMapping("/search")
    public ResponseEntity<Page<UserDto>> searchUsers(
            UserDto filter,
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(userService.searchUsers(filter, pageable));
    }

    // 5. 회원 등록(생성): POST /api/users
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(dto));
    }

    // 6. 회원 전체 수정: PUT /api/users/{userId}  (어색했던 /update 제거)
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long userId,
            @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.updateUser(userId, dto));
    }

    // 7. 회원 부분 수정: PATCH /api/users/{userId}  (어색했던 /update 제거)
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> patchUser(
            @PathVariable Long userId,
            @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.patchUser(userId, dto));
    }

    // 8. 비밀번호 변경: PATCH /api/users/{userId}/password  (어색했던 /update 제거)
    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    // 9. 회원 탈퇴(소프트 딜리트): PATCH /api/users/{userId}/withdraw  (어색했던 /update 제거)
    @PatchMapping("/{userId}/withdraw")
    public ResponseEntity<Void> withdrawUser(@PathVariable Long userId) {
        userService.withdrawUser(userId);
        return ResponseEntity.noContent().build();
    }

    // 10. 회원 영구 삭제(하드 딜리트): DELETE /api/users/{userId}  (어색했던 /delete 제거)
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
