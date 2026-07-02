package com.mjc.hotel.user.dto;

import com.mjc.hotel.user.entity.Membership;
import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchRequest {

    private String name;        // 이름 포함 검색
    private String email;       // 이메일 포함 검색
    private Role role;          // 역할 필터
    private Status status;      // 상태 필터
    private Membership membership; // 멤버십 필터

    // 페이징
    private int page = 0;
    private int size = 10;
    private String sortBy = "userId";   // 정렬 기준 (userId, name, createdAt)
    private String sortDir = "asc";     // asc / desc
}