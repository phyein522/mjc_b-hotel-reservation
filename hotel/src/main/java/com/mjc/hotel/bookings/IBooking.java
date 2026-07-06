package com.mjc.hotel.bookings;

import com.mjc.hotel.common.dto.IBase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface IBooking extends IBase {
    Long getBookingId();
    void setBookingId(Long bookingId);

    String getBookingNo();
    void setBookingNo(String bookingNo);

    String getGuestName();
    void setGuestName(String guestName);

    Nationality getNationality();
    void setNationality(Nationality nationality);

    String getGuestPhone();
    void setGuestPhone(String guestPhone);

    String getGuestEmail();
    void setGuestEmail(String guestEmail);

    String getSpecialRequest();
    void setSpecialRequest(String specialRequest);

    Integer getNights();
    void setNights(Integer nights);

    Integer getAdultCount();
    void setAdultCount(Integer adultCount);

    Integer getChildCount();
    void setChildCount(Integer childCount);

    LocalDate getCheckinDate();
    void setCheckinDate(LocalDate checkinDate);

    LocalDate getCheckoutDate();
    void setCheckoutDate(LocalDate checkoutDate);

    LocalTime getCheckinTime();
    void setCheckinTime(LocalTime checkinTime);

    LocalTime getCheckoutTime();
    void setCheckoutTime(LocalTime checkoutTime);

    Integer getPoint();
    void setPoint(Integer point);

    LocalDateTime getCancelledAt();
    void setCancelledAt(LocalDateTime cancelledAt);

    Integer getTotalAmount();
    void setTotalAmount(Integer totalAmount);

    Long getUserId();
    void setUserId(Long userId);

    Long getRoomId();
    void setRoomId(Long roomId);

    Long getHotelId();
    void setHotelId(Long hotelId);

    default IBooking copyMembers(IBooking src, boolean forced) {
        if(src == null) {
            return this;
        }
        if(forced || src.getBookingId() != null) {
            this.setBookingId(src.getBookingId());
        }
        if(forced || src.getBookingNo() != null) {
            this.setBookingNo(src.getBookingNo());
        }
        if(forced || src.getGuestName() != null) {
            this.setGuestName(src.getGuestName());
        }
        if(forced || src.getNationality() != null) {
            this.setNationality(src.getNationality());
        }
        if(forced || src.getGuestPhone() != null) {
            this.setGuestPhone(src.getGuestPhone());
        }
        if(forced || src.getGuestEmail() != null) {
            this.setGuestEmail(src.getGuestEmail());
        }
        if(forced || src.getSpecialRequest() != null) {
            this.setSpecialRequest(src.getSpecialRequest());
        }
        if(forced || src.getNights() != null) {
            this.setNights(src.getNights());
        }
        if(forced || src.getAdultCount() != null) {
            this.setAdultCount(src.getAdultCount());
        }
        if(forced || src.getChildCount() != null) {
            this.setChildCount(src.getChildCount());
        }
        if(forced || src.getCheckinDate() != null) {
            this.setCheckinDate(src.getCheckinDate());
        }
        if(forced || src.getCheckoutDate() != null) {
            this.setCheckoutDate(src.getCheckoutDate());
        }
        if(forced || src.getCheckinTime() != null) {
            this.setCheckinTime(src.getCheckinTime());
        }
        if(forced || src.getCheckoutTime() != null) {
            this.setCheckoutTime(src.getCheckoutTime());
        }
        if(forced || src.getPoint() != null) {
            this.setPoint(src.getPoint());
        }
        if(forced || src.getCancelledAt() != null) {
            this.setCancelledAt(src.getCancelledAt());
        }
        if(forced || src.getTotalAmount() != null) {
            this.setTotalAmount(src.getTotalAmount());
        }
        if(forced || src.getUserId() != null) {
            this.setUserId(src.getUserId());
        }
        if(forced || src.getRoomId() != null) {
            this.setRoomId(src.getRoomId());
        }
        if(forced || src.getHotelId() != null) {
            this.setHotelId(src.getHotelId());
        }
        return this;
    }
}
