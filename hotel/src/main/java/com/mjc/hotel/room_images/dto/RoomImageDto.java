package com.mjc.hotel.room_images.dto;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.rooms.dto.IRoom;
import com.mjc.hotel.rooms.dto.RoomDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
public class RoomImageDto extends BaseDto implements IRoomImage {
	private Long roomImageId;
	private String fileName;
	private Integer size;
	private String ext;
	private String storeName;
	private String path;
	private Long roomId;
	private RoomDto room;

	@Override
	public Long getRoomId() {
		// Long roomId 랑 room.getId() 랑 값이 항상 같도록 해야 한다.
		if ( this.room == null ) {
			this.room = new RoomDto();
		}
		if ( this.room.getRoomId() != null) {
			this.roomId = this.room.getRoomId();
		} else {
			this.room.setRoomId(this.roomId);
		}
		return this.room.getRoomId();
	}

	@Override
	public void setRoomId(Long roomId) {
		// Long roomId 랑 room.getId() 랑 값이 항상 같도록 해야 한다.
		if ( this.room == null ) {
			this.room = new RoomDto();
		}
		this.room.setRoomId(roomId);
		this.roomId = roomId;
	}

	@Override
	public void setRoom(IRoom room) {
		// Long roomId 랑 room.getId() 랑 값이 항상 같도록 해야 한다.
		if ( room == null ) {
			return;
		}
		if ( this.room == null ) {
			this.room = new RoomDto();
		}
		this.room.copyMembers(room, true);
		this.roomId = room.getRoomId();
	}
}
