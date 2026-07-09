package com.mjc.hotel.user.controller;

import com.mjc.hotel.common.ApiResponse;
import com.mjc.hotel.common.ResponseCode;
import com.mjc.hotel.user.dto.UserDto;
import com.mjc.hotel.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    @Autowired private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserDto>> signup(@RequestBody UserDto insertDto) {
        UserDto result = this.userService.insert(insertDto);
        return ResponseEntity.ok().body(
                new ApiResponse<>(ResponseCode.INSERT_OK, "signup success", result)
        );
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<UserDto>> update(@RequestBody UserDto updateDto) {
        UserDto result = this.userService.update(updateDto);
        return ResponseEntity.ok().body(
                new ApiResponse<>(ResponseCode.UPDATE_OK, "user update success", result)
        );
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> delete(@PathVariable Long userId) {
        UserDto result = this.userService.delete(userId);
        return ResponseEntity.ok().body(
                new ApiResponse<>(ResponseCode.DELETE_OK, "user delete success", result)
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> findById(@PathVariable Long userId) {
        UserDto result = this.userService.findById(userId);
        return ResponseEntity.ok().body(
                new ApiResponse<>(ResponseCode.SELECT_OK, "user findById success", result)
        );
    }
}
