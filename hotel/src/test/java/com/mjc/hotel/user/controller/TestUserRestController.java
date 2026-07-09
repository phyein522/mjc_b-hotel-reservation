package com.mjc.hotel.user.controller;

import com.mjc.hotel.user.dto.UserDto;
import com.mjc.hotel.user.entity.Membership;
import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.Status;
import com.mjc.hotel.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserRestController 의 모든 URL Mapping 메소드에 대한 단위 테스트
 *
 * @WebMvcTest 를 사용하여 웹 계층만 슬라이스 테스트하고,
 * UserService 는 @MockitoBean 으로 목(mock) 처리한다.
 *
 * 테스트 대상 URL
 * - POST   /api/users/signup
 * - PATCH  /api/users/update
 * - DELETE /api/users/delete/{userId}
 * - GET    /api/users/{userId}
 *
 * ApiResponse 의 실제 페이로드 필드명은 "responseData" 이다.
 * 예:
 * {
 *   "responseCode": "INSERT_OK",
 *   "message": "signup success",
 *   "responseData": { ... }
 * }
 */
@WebMvcTest(UserRestController.class)
public class TestUserRestController {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto sampleUserDto;

    @BeforeEach
    void setUp() {
        sampleUserDto = new UserDto();

        sampleUserDto.setUserId(1L);
        sampleUserDto.setEmail("test@example.com");
        sampleUserDto.setPassword("1234");
        sampleUserDto.setName("홍길동");
        sampleUserDto.setPhone("010-1234-5678");

        sampleUserDto.setRole(Role.CUSTOMER);
        sampleUserDto.setStatus(Status.ACTIVE);
        sampleUserDto.setMembership(Membership.NEW_MEMBER);

        sampleUserDto.setMarketingAgreed(true);
        sampleUserDto.setPoint(0);
    }

    @Test
    @DisplayName("POST /api/users/signup - 회원가입 성공")
    void signup_shouldReturnOk() throws Exception {
        // given
        when(userService.insert(any(UserDto.class))).thenReturn(sampleUserDto);

        // when & then
        mockMvc.perform(post("/api/users/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(sampleUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("INSERT_OK"))
                .andExpect(jsonPath("$.message").value("signup success"))

                .andExpect(jsonPath("$.responseData.userId").value(sampleUserDto.getUserId()))
                .andExpect(jsonPath("$.responseData.email").value(sampleUserDto.getEmail()))
                .andExpect(jsonPath("$.responseData.password").value(sampleUserDto.getPassword()))
                .andExpect(jsonPath("$.responseData.name").value(sampleUserDto.getName()))
                .andExpect(jsonPath("$.responseData.phone").value(sampleUserDto.getPhone()))

                .andExpect(jsonPath("$.responseData.role").value(Role.CUSTOMER.toString()))
                .andExpect(jsonPath("$.responseData.status").value(Status.ACTIVE.toString()))
                .andExpect(jsonPath("$.responseData.membership").value(Membership.NEW_MEMBER.toString()))

                .andExpect(jsonPath("$.responseData.marketingAgreed").value(sampleUserDto.getMarketingAgreed()))
                .andExpect(jsonPath("$.responseData.point").value(sampleUserDto.getPoint()));

        verify(userService, times(1)).insert(any(UserDto.class));
    }

    @Test
    @DisplayName("PATCH /api/users/update - 회원 정보 수정 성공")
    void update_shouldReturnOk() throws Exception {
        // given
        UserDto updateUserDto = new UserDto();

        updateUserDto.setUserId(1L);
        updateUserDto.setEmail("update@example.com");
        updateUserDto.setPassword("5678");
        updateUserDto.setName("김수정");
        updateUserDto.setPhone("010-9999-8888");

        updateUserDto.setRole(Role.CUSTOMER);
        updateUserDto.setStatus(Status.ACTIVE);
        updateUserDto.setMembership(Membership.STANDARD);

        updateUserDto.setMarketingAgreed(false);
        updateUserDto.setPoint(1000);

        when(userService.update(any(UserDto.class))).thenReturn(updateUserDto);

        // when & then
        mockMvc.perform(patch("/api/users/update")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("UPDATE_OK"))
                .andExpect(jsonPath("$.message").value("user update success"))

                .andExpect(jsonPath("$.responseData.userId").value(updateUserDto.getUserId()))
                .andExpect(jsonPath("$.responseData.email").value(updateUserDto.getEmail()))
                .andExpect(jsonPath("$.responseData.password").value(updateUserDto.getPassword()))
                .andExpect(jsonPath("$.responseData.name").value(updateUserDto.getName()))
                .andExpect(jsonPath("$.responseData.phone").value(updateUserDto.getPhone()))

                .andExpect(jsonPath("$.responseData.role").value(Role.CUSTOMER.toString()))
                .andExpect(jsonPath("$.responseData.status").value(Status.ACTIVE.toString()))
                .andExpect(jsonPath("$.responseData.membership").value(Membership.STANDARD.toString()))

                .andExpect(jsonPath("$.responseData.marketingAgreed").value(updateUserDto.getMarketingAgreed()))
                .andExpect(jsonPath("$.responseData.point").value(updateUserDto.getPoint()));

        verify(userService, times(1)).update(any(UserDto.class));
    }

    @Test
    @DisplayName("DELETE /api/users/delete/{userId} - 회원 삭제 성공")
    void delete_shouldReturnOk() throws Exception {
        // given
        Long userId = 1L;

        UserDto deleteUserDto = new UserDto();

        deleteUserDto.setUserId(1L);
        deleteUserDto.setEmail("test@example.com");
        deleteUserDto.setPassword("1234");
        deleteUserDto.setName("홍길동");
        deleteUserDto.setPhone("010-1234-5678");

        deleteUserDto.setRole(Role.CUSTOMER);
        deleteUserDto.setStatus(Status.WITHDRAW);
        deleteUserDto.setMembership(Membership.NEW_MEMBER);

        deleteUserDto.setMarketingAgreed(true);
        deleteUserDto.setPoint(0);

        when(userService.delete(userId)).thenReturn(deleteUserDto);

        // when & then
        mockMvc.perform(delete("/api/users/delete/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("DELETE_OK"))
                .andExpect(jsonPath("$.message").value("user delete success"))

                .andExpect(jsonPath("$.responseData.userId").value(deleteUserDto.getUserId()))
                .andExpect(jsonPath("$.responseData.email").value(deleteUserDto.getEmail()))
                .andExpect(jsonPath("$.responseData.password").value(deleteUserDto.getPassword()))
                .andExpect(jsonPath("$.responseData.name").value(deleteUserDto.getName()))
                .andExpect(jsonPath("$.responseData.phone").value(deleteUserDto.getPhone()))

                .andExpect(jsonPath("$.responseData.role").value(Role.CUSTOMER.toString()))
                .andExpect(jsonPath("$.responseData.status").value(Status.WITHDRAW.toString()))
                .andExpect(jsonPath("$.responseData.membership").value(Membership.NEW_MEMBER.toString()))

                .andExpect(jsonPath("$.responseData.marketingAgreed").value(deleteUserDto.getMarketingAgreed()))
                .andExpect(jsonPath("$.responseData.point").value(deleteUserDto.getPoint()));

        verify(userService, times(1)).delete(userId);
    }

    @Test
    @DisplayName("GET /api/users/{userId} - 회원 단건 조회 성공")
    void findById_shouldReturnOk() throws Exception {
        // given
        Long userId = 1L;

        when(userService.findById(userId)).thenReturn(sampleUserDto);

        // when & then
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("SELECT_OK"))
                .andExpect(jsonPath("$.message").value("user findById success"))

                .andExpect(jsonPath("$.responseData.userId").value(sampleUserDto.getUserId()))
                .andExpect(jsonPath("$.responseData.email").value(sampleUserDto.getEmail()))
                .andExpect(jsonPath("$.responseData.password").value(sampleUserDto.getPassword()))
                .andExpect(jsonPath("$.responseData.name").value(sampleUserDto.getName()))
                .andExpect(jsonPath("$.responseData.phone").value(sampleUserDto.getPhone()))

                .andExpect(jsonPath("$.responseData.role").value(Role.CUSTOMER.toString()))
                .andExpect(jsonPath("$.responseData.status").value(Status.ACTIVE.toString()))
                .andExpect(jsonPath("$.responseData.membership").value(Membership.NEW_MEMBER.toString()))

                .andExpect(jsonPath("$.responseData.marketingAgreed").value(sampleUserDto.getMarketingAgreed()))
                .andExpect(jsonPath("$.responseData.point").value(sampleUserDto.getPoint()));

        verify(userService, times(1)).findById(userId);
    }
}