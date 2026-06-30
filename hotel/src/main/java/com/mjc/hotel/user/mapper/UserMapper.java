package com.mjc.hotel.user.mapper;

import com.mjc.hotel.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    List<User> getUsers();

    User getUserById(@Param("userId") Long userId);

    int insertUser(User user);

    int updateUser(User user);

    int deleteUser(@Param("userId") Long userId);
}
