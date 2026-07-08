package com.mjc.hotel.user.service;

import com.mjc.hotel.user.dto.IUser;
import com.mjc.hotel.user.dto.PasswordChangeRequest;
import com.mjc.hotel.user.dto.UserDto;
import com.mjc.hotel.user.entity.Membership;
import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.Status;
import com.mjc.hotel.user.entity.User;
import com.mjc.hotel.user.exception.DuplicateEmailException;
import com.mjc.hotel.user.exception.UserNotFoundException;
import com.mjc.hotel.user.repository.UserRepository;
import com.mjc.hotel.user.repository.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    // ===== 조회 =====

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(e -> (UserDto) new UserDto().copyMembers(e, true))
                .toList();
    }

    public UserDto getUserById(Long userId) {
        return (UserDto) new UserDto().copyMembers(findUserOrThrow(userId), true);
    }

    public boolean checkEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    // 검색: UserDto의 name, email, role, status, membership 필드만 필터로 사용
    // 페이징은 Controller에서 @PageableDefault로 주입
    public Page<UserDto> searchUsers(UserDto filter, Pageable pageable) {
        return userRepository.findAll(UserSpecification.buildSpec(filter), pageable)
                .map(e -> (UserDto) new UserDto().copyMembers(e, true));
    }

    // ===== 생성 =====

    @Transactional
    public UserDto createUser(IUser dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException(dto.getEmail());
        }

        User user = (User) new User().copyMembers(dto, true);
        user.setUserId(null);

        // create 시 기본값 강제 설정 (요청값 무시)
        user.setRole(Role.CUSTOMER);
        user.setStatus(Status.ACTIVE);
        user.setMembership(Membership.BRONZE);

        return (UserDto) new UserDto().copyMembers(userRepository.save(user), true);
    }

    // ===== 전체 수정 (PUT) =====
    // role, status, membership은 PUT에서도 변경 가능 (관리자용)

    @Transactional
    public UserDto updateUser(Long userId, IUser dto) {
        User user = findUserOrThrow(userId);

        if (dto.getEmail() != null
                && !user.getEmail().equals(dto.getEmail())
                && userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException(dto.getEmail());
        }

        // forced=false: null 필드는 기존값 유지
        user.copyMembers(dto, false);

        return (UserDto) new UserDto().copyMembers(user, true);
    }

    // ===== 부분 수정 (PATCH) =====
    // 변경 가능 필드: email, name, phone, role, status, membership
    // password는 /password 엔드포인트로 분리

    @Transactional
    public UserDto patchUser(Long userId, IUser dto) {
        User user = findUserOrThrow(userId);

        // 이메일 변경 시 중복 체크
        if (dto.getEmail() != null
                && !user.getEmail().equals(dto.getEmail())
                && userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException(dto.getEmail());
        }

        // password는 PATCH에서 변경 불가 — 기존값으로 고정
        String currentPassword = user.getPassword();

        // forced=false: null 필드는 기존값 유지 (부분 수정)
        user.copyMembers(dto, false);

        // password 복원
        user.setPassword(currentPassword);

        return (UserDto) new UserDto().copyMembers(user, true);
    }

    // ===== 비밀번호 변경 =====

    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = findUserOrThrow(userId);

        if (!user.getPassword().equals(request.getCurrentPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(request.getNewPassword());
    }

    // ===== 탈퇴 (소프트 딜리트) =====

    @Transactional
    public void withdrawUser(Long userId) {
        User user = findUserOrThrow(userId);
        user.setStatus(Status.WITHDRAWN);
    }

    // ===== 하드 딜리트 =====

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.delete(findUserOrThrow(userId));
    }

    // ===== 내부 유틸 =====

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}