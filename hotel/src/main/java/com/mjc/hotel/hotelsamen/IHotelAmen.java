package com.mjc.hotel.hotelsamen;

import com.mjc.hotel.common.dto.IBase;

public interface IHotelAmen extends IBase {
    Long getAmenId();
    void setAmenId(Long amenId);

    Boolean getWifi();
    void setWifi(Boolean wifi);

    Boolean getPool();
    void setPool(Boolean swimPool);

    Boolean getGym();
    void setGym(Boolean gym);

    Boolean getSpa();
    void setSpa(Boolean spa);

    Boolean getRestaurant();
    void setRestaurant(Boolean restaurant);

    Boolean getValetParking();
    void setValetParking(Boolean valetParking);

    Boolean getBar();
    void setBar(Boolean bar);

    Long getHotelId();
    void setHotelId(Long hotelId);
    default IHotelAmen copyMembers(IHotelAmen source, boolean forced) {

        if (source == null) {
            return this;
        }

        IBase.super.copyMembers(source, forced);

        if (forced || source.getAmenId() != null) {
            this.setAmenId(source.getAmenId());
        }

        if (forced || source.getWifi() != null) {
            this.setWifi(source.getWifi());
        }
        if (forced || source.getPool() != null) {
            this.setPool(source.getPool());
        }
        if (forced || source.getGym() != null) {
            this.setGym(source.getGym());
        }
        if (forced || source.getSpa() != null) {
            this.setSpa(source.getSpa());
        }

        if (forced || source.getRestaurant() != null) {
            this.setRestaurant(source.getRestaurant());
        }

        if (forced || source.getValetParking() != null) {
            this.setValetParking(source.getValetParking());
        }

        if (forced || source.getBar() != null) {
            this.setBar(source.getBar());
        }

        if (forced || source.getHotelId() != null) {
            this.setHotelId(source.getHotelId());
        }

        return this;
    }
}
