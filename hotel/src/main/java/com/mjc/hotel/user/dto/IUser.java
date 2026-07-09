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

    Boolean getMarketingAgreed();
    void setMarketingAgreed(Boolean marketingAgreed);

    Integer getPoint();
    void setPoint(Integer point);

    default IUser copyMembers(IUser src, boolean forced) {
        if(src == null) {
            return this;
        }
        IBase.super.copyMembers(src, forced);
        if(forced || src.getUserId() != null) {
            this.setUserId(src.getUserId());
        }
        if(forced || src.getEmail() != null) {
            this.setEmail(src.getEmail());
        }
        if(forced || src.getPassword() != null) {
            this.setPassword(src.getPassword());
        }
        if(forced || src.getName() != null) {
            this.setName(src.getName());
        }
        if(forced || src.getPhone() != null) {
            this.setPhone(src.getPhone());
        }
        if(forced || src.getRole() != null) {
            this.setRole(src.getRole());
        }
        if(forced || src.getStatus() != null) {
            this.setStatus(src.getStatus());
        }
        if(forced || src.getMembership() != null) {
            this.setMembership(src.getMembership());
        }
        if(forced || src.getMarketingAgreed() != null) {
            this.setMarketingAgreed(src.getMarketingAgreed());
        }
        if(forced || src.getPoint() != null) {
            this.setPoint(src.getPoint());
        }
        return this;
    }
}
