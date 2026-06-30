package com.mjc.hotel.user.controller;

import com.mjc.hotel.user.entity.User;
import com.mjc.hotel.user.mapper.UserMapper;
import com.mjc.hotel.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    // ===== MyBatis 기반 =====

    @GetMapping("/mapperUsers")
    public List<User> getUsersByMapper() {
        return userMapper.getUsers();
    }

    @GetMapping("/mapperUsers/{userId}")
    public User getUserByMapper(@PathVariable Long userId) {
        return userMapper.getUserById(userId);
    }

    @PostMapping("/mapperUsers")
    public ResponseEntity<User> createUserByMapper(@RequestBody User user) {
        userMapper.insertUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/mapperUsers/{userId}")
    public ResponseEntity<User> updateUserByMapper(
            @PathVariable Long userId,
            @RequestBody User user) {
        user.setUserId(userId);
        userMapper.updateUser(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/mapperUsers/{userId}")
    public ResponseEntity<Void> deleteUserByMapper(@PathVariable Long userId) {
        userMapper.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // ===== JPA 기반 =====

    @GetMapping("/repositoryUsers")
    public List<User> getUsersByRepository() {
        return userRepository.findAll();
    }

    @GetMapping("/repositoryUsers/{userId}")
    public ResponseEntity<User> getUserByRepository(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/repositoryUsers")
    public ResponseEntity<User> createUserByRepository(@RequestBody User user) {
        User saved = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/repositoryUsers/{userId}")
    public ResponseEntity<User> updateUserByRepository(
            @PathVariable Long userId,
            @RequestBody User user) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        user.setUserId(userId);
        return ResponseEntity.ok(userRepository.save(user));
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
