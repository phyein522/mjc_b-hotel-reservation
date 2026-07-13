package com.mjc.hotel.room_images.dto;

import com.mjc.hotel.common.ServerPortListener;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
public class RoomImageResponseDto extends RoomImageListDto {
	private String imageUrl;

	public String getImageUrl() {
		if ( this.imageUrl == null ) {
			this.imageUrl = String.format("http://localhost:%d/api/roomimage/image/%d"
					, ServerPortListener.PORT
					, this.getRoomImageId());
		}
		return this.imageUrl;
	}
}
