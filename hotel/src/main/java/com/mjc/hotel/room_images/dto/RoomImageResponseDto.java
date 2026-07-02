package com.mjc.hotel.room_images.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
public class RoomImageResponseDto extends RoomImageDto {
	private String imageUrl;

	public String getImageUrl() {
		if ( this.imageUrl == null ) {
			this.imageUrl = String.format("http://localhost:8989/api/v1/roomimage/image/%d", this.getRoomImageId());
		}
		return this.imageUrl;
	}
}
