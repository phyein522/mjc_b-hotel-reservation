package com.mjc.hotel.user.dto;

import com.mjc.hotel.common.dto.IBase;
import com.mjc.hotel.user.entity.Membership;
import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.Status;

@tools.jackson.databind.annotation.JsonDeserialize(as = UserDto.class)
public interface IUser extends IBase {

    Long getUserId();
    void setUserId(Long userId);

    String getEmail();
    void setEmail(String email);

    String getPassword();
    void setPassword(String password);

    String getName();
    void setName(String name);

    String getPhone();
    void setPhone(String phone);

    Role getRole();
    void setRole(Role role);

    Status getStatus();
    void setStatus(Status status);

    Membership getMembership();
    void setMembership(Membership membership);

    String getSocialLogin();
    void setSocialLogin(String socialLogin);

    String getSocialLoginId();
    void setSocialLoginId(String socialLoginId);

    default IUser copyMembers(IUser source, boolean forced) {
        if (source == null) return this;
        IBase.super.copyMembers(source, forced);

        if (forced || source.getUserId() != null)
            this.setUserId(source.getUserId());
        if (forced || source.getEmail() != null)
            this.setEmail(source.getEmail());
        if (forced || source.getPassword() != null)
            this.setPassword(source.getPassword());
        if (forced || source.getName() != null)
            this.setName(source.getName());
        if (forced || source.getPhone() != null)
            this.setPhone(source.getPhone());
        if (forced || source.getRole() != null)
            this.setRole(source.getRole());
        if (forced || source.getStatus() != null)
            this.setStatus(source.getStatus());
        if (forced || source.getMembership() != null)
            this.setMembership(source.getMembership());
        if (forced || source.getSocialLogin() != null)
            this.setSocialLogin(source.getSocialLogin());
        if (forced || source.getSocialLoginId() != null)
            this.setSocialLoginId(source.getSocialLoginId());

        return this;
    }
}