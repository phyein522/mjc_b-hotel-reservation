package com.mjc.hotel.user.service;

import com.mjc.hotel.user.dto.*;
import com.mjc.hotel.user.entity.Status;
import com.mjc.hotel.user.entity.User;
import com.mjc.hotel.user.exception.DuplicateEmailException;
import com.mjc.hotel.user.exception.UserNotFoundException;
import com.mjc.hotel.user.repository.UserRepository;
import com.mjc.hotel.user.repository.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    // 전체 조회
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    // 단건 조회
    public UserResponse getUserById(Long userId) {
        return UserResponse.from(findUserOrThrow(userId));
    }

    // 이메일 중복 체크
    public boolean checkEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    // 페이징 + 복합 검색
    public Page<UserResponse> searchUsers(UserSearchRequest request) {
        Sort sort = request.getSortDir().equalsIgnoreCase("desc")
                ? Sort.by(request.getSortBy()).descending()
                : Sort.by(request.getSortBy()).ascending();

        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize(), sort);

        return userRepository.findAll(
                UserSpecification.buildSpec(request), pageRequest
        ).map(UserResponse::from);
    }

    // 생성
    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }
        return UserResponse.from(userRepository.save(request.toEntity()));
    }

    // 전체 수정
    @Transactional
    public UserResponse updateUser(Long userId, UserRequest request) {
        User user = findUserOrThrow(userId);

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // 개발용 평문 저장
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());
        user.setMembership(request.getMembership());

        return UserResponse.from(user);
    }

    // 부분 수정
    @Transactional
    public UserResponse patchUser(Long userId, UserPatchRequest request) {
        User user = findUserOrThrow(userId);

        if (request.getName() != null)       user.setName(request.getName());
        if (request.getPhone() != null)      user.setPhone(request.getPhone());
        if (request.getStatus() != null)     user.setStatus(request.getStatus());
        if (request.getMembership() != null) user.setMembership(request.getMembership());

        return UserResponse.from(user);
    }

    // 비밀번호 변경 (개발용 평문)
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = findUserOrThrow(userId);

        if (!user.getPassword().equals(request.getCurrentPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(request.getNewPassword());
    }

    // 소프트 딜리트 (탈퇴 처리)
    @Transactional
    public void withdrawUser(Long userId) {
        User user = findUserOrThrow(userId);
        user.setStatus(Status.WITHDRAWN);
    }

    // 하드 딜리트
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.delete(findUserOrThrow(userId));
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}