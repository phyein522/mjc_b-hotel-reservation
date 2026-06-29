package com.mjc.hotel.user.entity;

import com.mjc.hotel.util.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String email;
    private String password;
    private String name;
    private String phone;
    private String socialLogin;
    private String socialLoginId;
}