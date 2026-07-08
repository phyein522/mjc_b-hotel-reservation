package com.mjc.hotel.room_images.dto;

import com.mjc.hotel.common.dto.BaseEntity;
import com.mjc.hotel.rooms.dto.IRoom;
import com.mjc.hotel.rooms.dto.RoomEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
@Entity(name = "room_images")
public class RoomImageEntity extends BaseEntity implements IRoomImage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "room_image_id")
	private Long roomImageId;

	@Column(name = "file_name", nullable = false, length = 100)
	private String fileName;

	@Column(nullable = false)
	private Integer size;

	@Column(nullable = false, length = 10)
	private String ext;

	@Column(name = "store_name", nullable = false, length = 100)
	private String storeName;

	@Column(nullable = false, length = 100)
	private String path;

	@Transient
	private Long roomId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id", nullable = false)
	private RoomEntity room;

	@Override
	public Long getRoomId() {
		if ( this.room == null ) {
			this.room = new RoomEntity();
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
		// Long 외래키값 과 객체.기본키 값을 항상 같도록 해야 한다.
		if ( this.room == null ) {
			this.room = new RoomEntity();
		}
		this.room.setRoomId(roomId);
		this.roomId = roomId;
	}

	@Override
	public void setRoom(IRoom room) {
		// Long 외래키값 과 객체.기본키 값을 항상 같도록 해야 한다.
		if ( room == null ) {
			return;
		}
		if ( this.room == null ) {
			this.room = new RoomEntity();
		}
		this.room.copyMembers(room, true);
		this.roomId = room.getRoomId();
	}
}
