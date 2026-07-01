package com.mjc.hotel.rooms.dto;

import com.mjc.hotel.room_images.dto.RoomImageDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomInsertResponseDto {
	private RoomDto roomDto;
	private List<RoomImageDto> roomImageDtos;
}
