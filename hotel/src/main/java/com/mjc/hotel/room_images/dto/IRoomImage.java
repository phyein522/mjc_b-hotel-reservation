package com.mjc.hotel.room_images.dto;

import com.mjc.hotel.common.dto.IBase;
import com.mjc.hotel.rooms.dto.IRoom;

public interface IRoomImage extends IBase {
	Long getRoomImageId();
	void setRoomImageId(Long roomImageId);

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

	Long getRoomId();
	void setRoomId(Long roomId);

	IRoom getRoom();
	void setRoom(IRoom room);

	default IRoomImage copyMembers(IRoomImage src, boolean forced) {
		if ( src == null ) {
			return this;
		}
		IBase.super.copyMembers(src, forced);
		if ( forced || src.getRoomImageId() != null ) {
			this.setRoomImageId(src.getRoomImageId());
		}
		if ( forced || src.getFileName() != null ) {
			this.setFileName(src.getFileName());
		}
		if ( forced || src.getSize() != null ) {
			this.setSize(src.getSize());
		}
		if ( forced || src.getExt() != null ) {
			this.setExt(src.getExt());
		}
		if ( forced || src.getStoreName() != null ) {
			this.setStoreName(src.getStoreName());
		}
		if ( forced || src.getPath() != null ) {
			this.setPath(src.getPath());
		}
		if ( forced || src.getRoomId() != null ) {
			this.setRoomId(src.getRoomId());
		}
		if ( forced || src.getRoomId() != null ) {
			this.setRoomId(src.getRoomId());
			this.getRoom().copyMembers(src.getRoom(), forced);
		}
		return this;
	}
}
