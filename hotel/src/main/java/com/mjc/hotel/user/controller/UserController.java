package com.mjc.hotel.user.controller;

import com.mjc.hotel.user.entity.User;
import com.mjc.hotel.user.mapper.UserMapper;
import com.mjc.hotel.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {


    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @GetMapping("/mapperUsers")
    public List<User> getUsers() {
        return userMapper.getUsers();
    }
    @GetMapping("/repositoryUsers")
    public List<User> getSUser2() {
        return userRepository.findAll();
    }

    @PostMapping("/repositoryUsers")
    public List<User> postUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/mapperUsers")
    public List<User> postUser2() {
        return userRepository.findAll();
    }
}