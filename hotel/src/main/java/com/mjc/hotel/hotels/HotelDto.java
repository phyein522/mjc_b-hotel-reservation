package com.mjc.hotel.hotels;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.user.dto.IUser;
import com.mjc.hotel.user.dto.UserDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class HotelDto extends BaseDto implements IHotel{
    private Long hotelId;
    private String name;
    private String description;
    private String address;
    private String city;
    private String zipCode;
    private String phone;
    private String email;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private int starRate;
    private Boolean isActive;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private HotelTypeEnum type;

    private Long userId;
    private UserDto user;

    @Override
    public Long getUserId() {

        if (user != null) {
            return user.getUserId();
        }

        return userId;
    }

    @Override
    public void setUserId(Long userId) {

        this.userId = userId;

        if (user == null) {
            user = new UserDto();
        }

        user.setUserId(userId);
    }
    @Override
    public void setUser(IUser user) {

        if (user == null) {
            return;
        }

        if (this.user == null) {
            this.user = new UserDto();
        }

        this.user.copyMembers(user, true);
        this.userId = user.getUserId();
    }
}
