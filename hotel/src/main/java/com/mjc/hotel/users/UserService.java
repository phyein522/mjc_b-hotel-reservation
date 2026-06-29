package com.mjc.hotel.users;

import com.mjc.hotel.users.UserRequest;
import com.mjc.hotel.users.UserResponse;
import com.mjc.hotel.users.User;
import com.mjc.hotel.users.Role;
import com.mjc.hotel.users.Status;
import com.mjc.hotel.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 전체 조회
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    // 단건 조회
    public UserResponse getUserById(Long userId) {
        User user = findUserOrThrow(userId);
        return UserResponse.from(user);
    }

    // 이메일로 조회
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자가 없습니다: " + email));
        return UserResponse.from(user);
    }

    // 역할별 조회
    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    // 상태별 조회
    public List<UserResponse> getUsersByStatus(Status status) {
        return userRepository.findByStatus(status)
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    // 생성
    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .role(request.getRole())
                .status(request.getStatus())
                .socialLogin(request.getSocialLogin())
                .socialLoginId(request.getSocialLoginId())
                .membership(request.getMembership())
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    // 수정
    @Transactional
    public UserResponse updateUser(Long userId, UserRequest request) {
        User user = findUserOrThrow(userId);

        // 이메일 변경 시 중복 체크
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + request.getEmail());
        }

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());
        user.setSocialLogin(request.getSocialLogin());
        user.setSocialLoginId(request.getSocialLoginId());
        user.setMembership(request.getMembership());

        return UserResponse.from(user);
    }

    // 삭제
    @Transactional
    public void deleteUser(Long userId) {
        User user = findUserOrThrow(userId);
        userRepository.delete(user);
    }

    // 상태 변경 (탈퇴, 잠금 등)
    @Transactional
    public UserResponse changeStatus(Long userId, Status status) {
        User user = findUserOrThrow(userId);
        user.setStatus(status);
        return UserResponse.from(user);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));
    }
}
