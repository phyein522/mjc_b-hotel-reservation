package com.mjc.hotel.promotion;

import com.mjc.hotel.common.dto.IBase;

import java.math.BigDecimal;
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

    BigDecimal getDisValue();
    void setDisValue(BigDecimal disValue);

    LocalDateTime getStartDate();
    void setStartDate(LocalDateTime startDate);

    LocalDateTime getEndDate();
    void setEndDate(LocalDateTime endDate);

    Integer getResCount();
    void setResCount(Integer resCount);

    PromotionStateTypeEnum getStatus();
    void setStatus(PromotionStateTypeEnum status);

    Long getRoomId();
    void setRoomId(Long roomId);

    IRoom getRoom();
    void setRoom(IRoom room);

    Long getUserId();
    void setUserId(Long userId);

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
                if (source.getRoom() != null) {
                    this.setRoom(source.getRoom());
                }
            } catch (LazyInitializationException e) {
                System.err.println(e.getMessage());
            }
        }
        if (forced || source.getUserId() != null) {
            this.setUserId(source.getUserId());
        }
        return this;
    }
}
