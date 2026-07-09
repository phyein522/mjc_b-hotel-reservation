package com.mjc.hotel.user.entity;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.user.dto.IUser;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity extends BaseEntity implements IUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true, comment = "이메일(로그인 시 사용)")
    private String email;

    @Column(nullable = false, comment = "비밀번호")
    private String password;

    @Column(nullable = false, length = 100, comment = "이름")
    private String name;

    @Column(nullable = false, length = 30, comment = "전화번호")
    private String phone;

    @Column(nullable = false, comment = "권한")
    private Role role;

    @Column(nullable = false, comment = "상태")
    private Status status;

    @Column(nullable = false, comment = "멤버십 등급")
    private Membership membership;

    @Column(nullable = false, comment = "마케팅 정보 수신 동의")
    private Boolean marketingAgreed;

    @Column(nullable = false, comment = "보유 포인트")
    private Integer point;
}
