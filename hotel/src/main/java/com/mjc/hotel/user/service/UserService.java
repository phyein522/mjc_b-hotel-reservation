package com.mjc.hotel.user.service;

import com.mjc.hotel.user.dto.UserDto;
import com.mjc.hotel.user.entity.Membership;
import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.Status;
import com.mjc.hotel.user.entity.UserEntity;
import com.mjc.hotel.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserDto insert(UserDto insertDto) {
        UserEntity insertEntity = (UserEntity)new UserEntity().copyMembers(insertDto, true);

        insertEntity.setUserId(null);
        insertEntity.setRole(Role.CUSTOMER);
        insertEntity.setStatus(Status.ACTIVE);
        insertEntity.setMembership(Membership.NEW_MEMBER);
        insertEntity.setPoint(0);

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
}
