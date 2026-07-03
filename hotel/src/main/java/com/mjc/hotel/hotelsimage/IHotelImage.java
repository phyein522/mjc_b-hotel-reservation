package com.mjc.hotel.hotelsimage;

import com.mjc.hotel.common.dto.IBase;
import com.mjc.hotel.hotels.IHotel;

public interface IHotelImage extends IBase {

    Long getHotelImageId();
    void setHotelImageId(Long hotelImageId);

    String getFileName();
    void setFileName(String fileName);

    Integer getSize();
    void setSize(Integer size);

    String getExt();
    void setExt(String ext);

    String getStoreName();
    void setStoreName(String storeName);

    String getPath();
    void setPath(String path);

    Long getHotelId();
    void setHotelId(Long hotelId);

    IHotel getHotel();
    void setHotel(IHotel hotel);

    default IHotelImage copyMembers(IHotelImage source, boolean forced) {

        if(source == null){
            return this;
        }

        IBase.super.copyMembers(source, forced);

        if(forced || source.getHotelImageId()!=null){
            this.setHotelImageId(source.getHotelImageId());
        }

        if(forced || source.getFileName()!=null){
            this.setFileName(source.getFileName());
        }

        if(forced || source.getSize()!=null){
            this.setSize(source.getSize());
        }

        if(forced || source.getExt()!=null){
            this.setExt(source.getExt());
        }

        if(forced || source.getStoreName()!=null){
            this.setStoreName(source.getStoreName());
        }

        if(forced || source.getPath()!=null){
            this.setPath(source.getPath());
        }

        if(forced || source.getHotelId()!=null){
            this.setHotelId(source.getHotelId());
        }

        if (forced && source.getHotel() != null) {
            this.setHotel(source.getHotel());
        }
        return this;
    }
}