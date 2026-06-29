package com.mjc.hotel.user.entity;

import com.mjc.hotel.util.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User extends BaseEntity {
    @jakarta.persistence.Id
    private Long id;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String email;
    private String password;
    private String name;
    private String phone;
    private String socialLogin;
    private String socialLoginId;
}