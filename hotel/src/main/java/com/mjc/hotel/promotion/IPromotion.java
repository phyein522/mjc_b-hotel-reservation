package com.mjc.hotel.promotion;

import com.mjc.hotel.common.dto.IBase;
import java.time.LocalDateTime;
import com.mjc.hotel.rooms.dto.IRoom;
import org.hibernate.LazyInitializationException;

@tools.jackson.databind.annotation.JsonDeserialize(as = PromotionDto.class)
public interface IPromotion extends IBase {
    Long getProId();
    void setProId(Long proId);

    String getName();
    void setName(String name);

    String  getDescription();
    void setDescription(String description);

    DiscountTypeEnum getDisType();
    void setDisType(DiscountTypeEnum disType);

    String getDisValue();
    void setDisValue(String disValue);

    LocalDateTime getStartDate();
    void setStartDate(LocalDateTime startDate);

    LocalDateTime getEndDate();
    void setEndDate(LocalDateTime endDate);

    Integer getResCount();
    void setResCount(Integer resCount);

    String getStatus();
    void setStatus(String status);

    Long getRoomId();
    void setRoomId(Long roomId);

    IRoom getRoom();
    void setRoom(IRoom room);

    default IPromotion copyMembers(IPromotion source, boolean forced) {

        if (source == null) {
            return this;
        }

        IBase.super.copyMembers(source, forced);

        if (forced || source.getProId() != null) {
            this.setProId(source.getProId());
        }

        if (forced || source.getName() != null) {
            this.setName(source.getName());
        }
        if (forced || source.getDescription() != null) {
            this.setDescription(source.getDescription());
        }
        if (forced || source.getDisType() != null) {
            this.setDisType(source.getDisType());
        }
        if (forced || source.getDisValue() != null) {
            this.setDisValue(source.getDisValue());
        }

        if (forced || source.getStartDate() != null) {
            this.setStartDate(source.getStartDate());
        }

        if (forced || source.getEndDate() != null) {
            this.setEndDate(source.getEndDate());
        }

        if (forced || source.getResCount() != null) {
            this.setResCount(source.getResCount());
        }

        if (forced || source.getStatus() != null) {
            this.setStatus(source.getStatus());
        }

        if (forced || source.getRoomId() != null) {
            this.setRoomId(source.getRoomId());

            try {
                this.getRoom().copyMembers(source.getRoom(), forced);
            } catch (LazyInitializationException e) {
                System.err.println(e.getMessage());
            }
        }
        return this;
    }
}
