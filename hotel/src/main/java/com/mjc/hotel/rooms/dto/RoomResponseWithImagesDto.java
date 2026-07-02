package com.mjc.hotel.rooms.dto;

import com.mjc.hotel.room_images.dto.RoomImageResponseDto;
import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponseWithImagesDto {
	private RoomDto roomDto;
	private Page<RoomImageResponseDto> roomImages;
}
