package com.mjc.hotel.hotels;


import com.mjc.hotel.common.dto.IBase;
import java.time.LocalTime;

@tools.jackson.databind.annotation.JsonDeserialize(as = HotelDto.class)
public interface IHotel extends IBase {
    Long getHotelId();
    void setHotelId(Long hotelId);

    String getName();
    void setName(String name);

    String getDescription();
    void setDescription(String description);

    String getAddress();
    void setAddress(String address);

    String getCity();
    void setCity(String city);

    String getZipCode();
    void setZipCode(String zipCode);

    String getPhone();
    void setPhone(String phone);

    String getEmail();
    void setEmail(String email);

    LocalTime getCheckIn();
    void setCheckIn(LocalTime checkIn);

    LocalTime getCheckOut();
    void setCheckOut(LocalTime checkOut);

    Short getStarRate();
    void setStarRate(Short starRate);

    Boolean getIsActive();
    void setIsActive(Boolean isActive);

    String getLatitude();
    void setLatitude(String latitude);

    String getLongitude();
    void setLongitude(String longitude);

    HotelTypeEnum getType();
    void setType(HotelTypeEnum type);

    default IHotel copyMembers(IHotel source, boolean forced) {
        if ( source == null ) {
            return this;
        }
        IBase.super.copyMembers(source, forced);
        if ( forced || source.getHotelId() != null ) {
            this.setHotelId(source.getHotelId());
        }
        if ( forced || source.getName() != null ) {
            this.setName(source.getName());
        }
        if ( forced || source.getDescription() != null ) {
            this.setDescription(source.getDescription());
        }
        if ( forced || source.getAddress() != null ) {
            this.setAddress(source.getAddress());
        }
        if ( forced || source.getCity() != null ) {
            this.setCity(source.getCity());
        }
        if ( forced || source.getZipCode() != null ) {
            this.setZipCode(source.getZipCode());
        }
        if ( forced || source.getPhone() != null ) {
            this.setPhone(source.getPhone());
        }
        if ( forced || source.getEmail() != null ) {
            this.setEmail(source.getEmail());
        }
        if ( forced || source.getCheckIn() != null ) {
            this.setCheckIn(source.getCheckIn());
        }
        if ( forced || source.getCheckOut() != null ) {
            this.setCheckOut(source.getCheckOut());
        }
        if ( forced || source.getStarRate() != null ) {
            this.setStarRate(source.getStarRate());
        }
        if ( forced || source.getIsActive() != null ) {
            this.setIsActive(source.getIsActive());
        }
        if ( forced || source.getLatitude() != null ) {
            this.setLatitude(source.getLatitude());
        }
        if ( forced || source.getLongitude() != null ) {
            this.setLongitude(source.getLongitude());
        }
        if ( forced || source.getType() != null ) {
            this.setType(source.getType());
        }
        return this;
    }
}
