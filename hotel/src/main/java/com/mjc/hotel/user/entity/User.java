package com.mjc.hotel.user.entity;

import com.mjc.hotel.util.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private Role role;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long user_ID;
    private String email;
    private String password;
    private String name;
    private String phone;
    private Enum Role;
    private Enum Status;
    //private Timestamp created_at;
    //private Timestamp updated_at;
    private String socialLogin;
    private String socialLoginId;
    private Enum membership;


}
