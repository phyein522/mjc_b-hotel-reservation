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

    // GET /api/users
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET /api/users/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // GET /api/users/check-email?email=
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(Map.of("available", userService.checkEmailAvailable(email)));
    }

    // GET /api/users/search
    // 검색 파라미터: name, email, role, status, membership (UserDto 필드 직접 사용)
    // 페이징 파라미터: page, size, sort (Spring 기본 Pageable 파라미터)
    // 예: /api/users/search?name=홍&status=ACTIVE&page=0&size=10&sort=createdAt,desc
    @GetMapping("/search")
    public ResponseEntity<Page<UserDto>> searchUsers(
            UserDto filter,
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(userService.searchUsers(filter, pageable));
    }

    // POST /api/users — 생성
    // role, status, membership은 서버에서 자동 설정 (CUSTOMER / ACTIVE / BRONZE)
    // 요청에 포함해도 무시됨
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(dto));
    }

    // PUT /api/users/{userId} — 전체 수정 (null 필드는 기존값 유지)
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long userId,
            @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.updateUser(userId, dto));
    }

    // PATCH /api/users/{userId} — 부분 수정
    // 변경 가능 필드: email, name, phone, role, status, membership
    // password는 /password 엔드포인트 사용
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> patchUser(
            @PathVariable Long userId,
            @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.patchUser(userId, dto));
    }

    // PATCH /api/users/{userId}/password — 비밀번호 변경
    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/users/{userId}/withdraw — 탈퇴 처리 (status = WITHDRAWN)
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