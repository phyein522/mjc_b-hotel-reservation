package com.mjc.hotel.hotelsamen;

import com.mjc.hotel.common.dto.IBase;
import com.mjc.hotel.hotels.IHotel;
import org.hibernate.LazyInitializationException;

@tools.jackson.databind.annotation.JsonDeserialize(as = HotelAmenDto.class)
public interface IHotelAmen extends IBase {
    Long getAmenId();
    void setAmenId(Long amenId);

    Boolean getWifi();
    void setWifi(Boolean wifi);

    Boolean getPool();
    void setPool(Boolean swimPool);

    Boolean getFitnessCenter();
    void setFitnessCenter(Boolean fitnessCenter);

    Boolean getSpa();
    void setSpa(Boolean spa);

    Boolean getRestaurant();
    void setRestaurant(Boolean restaurant);

    Boolean getValetParking();
    void setValetParking(Boolean valetParking);

    Boolean getFreeParking();
    void setFreeParking(Boolean freeParking);

    Boolean getConcierge();
    void setConcierge(Boolean concierge);

    Boolean getBar();
    void setBar(Boolean bar);

    Boolean getBreakfast();
    void setBreakfast(Boolean breakfast);

    Boolean getAirportShuttle();
    void setAirportShuttle(Boolean airportShuttle);

    Boolean getRoomService();
    void setRoomService(Boolean roomService);

    Boolean getLaundry();
    void setLaundry(Boolean laundry);

    Boolean getLounge();
    void setLounge(Boolean lounge);

    Boolean getSauna();
    void setSauna(Boolean sauna);

    Boolean getFreeCancel();
    void setFreeCancel(Boolean freeCancel);

    Boolean getPetFriendly();
    void setPetFriendly(Boolean petFriendly);

    Long getHotelId();
    void setHotelId(Long hotelId);

    IHotel getHotel();
    void setHotel(IHotel hotel);

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
        if (forced || source.getFitnessCenter() != null) {
            this.setFitnessCenter(source.getFitnessCenter());
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
        if (forced || source.getFreeParking() != null) {
            this.setFreeParking(source.getFreeParking());
        }
        if (forced || source.getConcierge() != null) {
            this.setConcierge(source.getConcierge());
        }

        if (forced || source.getBar() != null) {
            this.setBar(source.getBar());
        }
        if (forced || source.getBreakfast() != null) {
            this.setBreakfast(source.getBreakfast());
        }
        if (forced || source.getAirportShuttle() != null) {
            this.setAirportShuttle(source.getAirportShuttle());
        }
        if (forced || source.getRoomService() != null) {
            this.setRoomService(source.getRoomService());
        }
        if (forced || source.getLaundry() != null) {
            this.setLaundry(source.getLaundry());
        }
        if (forced || source.getLounge() != null) {
            this.setLounge(source.getLounge());
        }
        if (forced || source.getSauna() != null) {
            this.setSauna(source.getSauna());
        }
        if (forced || source.getFreeCancel() != null) {
            this.setFreeCancel(source.getFreeCancel());
        }
        if (forced || source.getPetFriendly() != null) {
            this.setPetFriendly(source.getPetFriendly());
        }

        if (forced || source.getHotelId() != null) {
            this.setHotelId(source.getHotelId());

            try {
                if (source.getHotel() != null) {
                    this.setHotel(source.getHotel());
                }
            } catch (LazyInitializationException e) {
                System.err.println(e.getMessage());
            }
        }

        return this;
    }
}
