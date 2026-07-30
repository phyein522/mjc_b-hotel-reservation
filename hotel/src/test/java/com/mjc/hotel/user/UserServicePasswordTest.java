package com.mjc.hotel.user;

import com.mjc.hotel.user.dto.UserDto;
import com.mjc.hotel.user.entity.UserEntity;
import com.mjc.hotel.user.repository.UserRepository;
import com.mjc.hotel.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServicePasswordTest {
    @Mock UserRepository userRepository;

    @Test
    void insertStoresBcryptPassword() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        UserService userService = new UserService(this.userRepository, passwordEncoder);
        UserDto request = UserDto.builder()
                .email("user@example.com")
                .password("plain-password")
                .name("User")
                .phone("010-1234-5678")
                .build();
        when(this.userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(false);
        when(this.userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setUserId(1L);
            return entity;
        });

        userService.insert(request);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(this.userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isNotEqualTo("plain-password");
        assertThat(passwordEncoder.matches("plain-password", captor.getValue().getPassword())).isTrue();
    }
}
