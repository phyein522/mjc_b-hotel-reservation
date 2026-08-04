package com.mjc.hotel.user.service;

import com.mjc.hotel.user.dto.UserDto;
import com.mjc.hotel.user.entity.Membership;
import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.Status;
import com.mjc.hotel.user.entity.UserEntity;
import com.mjc.hotel.user.exception.DuplicateEmailException;
import com.mjc.hotel.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto insert(UserDto insertDto) {
        String email = insertDto.getEmail() == null ? "" : insertDto.getEmail().trim().toLowerCase();
        if (email.isBlank()) {
            throw new IllegalArgumentException("이메일을 입력해주세요.");
        }
        if (this.userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException("이미 가입된 이메일입니다.");
        }
        insertDto.setEmail(email);
        if (!StringUtils.hasText(insertDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }
        insertDto.setPassword(this.passwordEncoder.encode(insertDto.getPassword()));
        UserEntity insertEntity = (UserEntity)new UserEntity().copyMembers(insertDto, true);

        insertEntity.setUserId(null);
        insertEntity.setRole(Role.CUSTOMER);
        insertEntity.setStatus(Status.ACTIVE);
        insertEntity.setMembership(Membership.NEW_MEMBER);
        insertEntity.setPoint(0);
        insertEntity.setMarketingAgreed(Boolean.TRUE.equals(insertDto.getMarketingAgreed()));
        insertEntity.setPhone(insertDto.getPhone() == null ? "" : insertDto.getPhone().trim());
        insertEntity.setEmailVerified(false);

        UserEntity save = this.userRepository.save(insertEntity);
        UserDto result = (UserDto)new UserDto().copyMembers(save, true);
        return result;
    }

    public UserDto update(UserDto updateDto) {
        UserDto findDto = this.findById(updateDto.getUserId());
        findDto.copyMembers(updateDto, false);
        UserEntity updateEntity = (UserEntity)new UserEntity().copyMembers(updateDto, true);
        UserEntity save = this.userRepository.save(updateEntity);
        UserDto result = (UserDto)new UserDto().copyMembers(save, true);
        return result;
    }

    public UserDto findById(Long userId) {
        UserEntity findEntity = this.userRepository.findById(userId).orElseThrow();
        UserDto result = (UserDto)new UserDto().copyMembers(findEntity, true);
        return result;
    }

    public UserDto delete(Long userId) {
        UserDto result = this.findById(userId);
        this.userRepository.deleteById(userId);
        return result;
    }
    public UserDto findByEmail(String email) {
        UserEntity findEntity = this.userRepository.findByEmailIgnoreCase(email).orElseThrow();
        UserDto result = (UserDto)new UserDto().copyMembers(findEntity, true);
        return result;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.findByEmail(username);
    }
}
