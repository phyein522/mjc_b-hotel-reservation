package com.mjc.hotel.hotelsimage;

import com.mjc.hotel.common.dto.IBase;

public interface IHotelImage extends IBase {

    Long getImageId();
    void setImageId(Long imageId);

    String getUrl();
    void setUrl(String url);

    Integer getSortOrder();
    void setSortOrder(Integer sortOrder);

    Boolean getIsThumbnail();
    void setIsThumbnail(Boolean isThumbnail);

    Long getHotelId();
    void setHotelId(Long hotelId);

    default IHotelImage copyMembers(IHotelImage source, boolean forced) {

        if(source == null){
            return this;
        }

        IBase.super.copyMembers(source, forced);

        if(forced || source.getImageId()!=null){
            this.setImageId(source.getImageId());
        }

        if(forced || source.getUrl()!=null){
            this.setUrl(source.getUrl());
        }

        if(forced || source.getSortOrder()!=null){
            this.setSortOrder(source.getSortOrder());
        }

        if(forced || source.getIsThumbnail()!=null){
            this.setIsThumbnail(source.getIsThumbnail());
        }

        if(forced || source.getHotelId()!=null){
            this.setHotelId(source.getHotelId());
        }

        return this;
    }
}