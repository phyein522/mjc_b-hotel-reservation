package com.mjc.hotel.user;


import com.mjc.hotel.user.entity.Membership;
import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.Status;
import com.mjc.hotel.user.entity.User;
import com.mjc.hotel.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;



@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("1. 테스트 유저 10개 추가")
    public void addTestUser() {

        for (int i = 0; i < 10; i++) {

            User u = User.builder()
                    .email("test" + i + "@naver.com")
                    .password("1234")
                    .name("test" + i)
                    .phone("0102020303" + i)
                    .role(Role.ADMIN)
                    .status(Status.ACTIVE)
                    .membership(Membership.platinum)
                    .build();

            userRepository.save(u);
        }
    }
}

