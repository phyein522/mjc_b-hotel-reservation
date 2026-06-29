package com.mjc.hotel.user;


import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.User;
import com.mjc.hotel.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Commit
    @DisplayName("1. 테스트 유저 10개 추가")
    public void addTestUser() {

        for (int i =0; i < 12; i ++) {
            User u = User
                    .builder()
                    .user_ID(11+i)
                    .email("test" + i + "@naver.com")
                    .password("1234")
                    .name("test" + i)
                    .phone("0102020303" + i)
                    .Role(Role.valueOf("ADMIN"))
                    .Status(Role.valueOf("Active"))
                    .socialLoginId("kakao")
                    .socialLogin("")
                    //.created_at(Timestamp.valueOf(LocalDateTime.now()))
                    //.updated_at(Timestamp.valueOf(LocalDateTime.now()))
                    .membership(Role.valueOf("gold"))
                    .build();

            userRepository.save(u);
        }
    }
}

