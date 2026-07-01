package com.mjc.hotel.room_images.service;

import com.mjc.hotel.room_images.dto.IRoomImage;
import com.mjc.hotel.room_images.dto.RoomImageDto;
import com.mjc.hotel.room_images.dto.RoomImageEntity;
import com.mjc.hotel.rooms.dto.IRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomImageService {
	@Autowired
	private RoomImageRepository roomImageRepository;

	public RoomImageDto insert(IRoomImage roomDto) {
		RoomImageEntity insertEntity = (RoomImageEntity) new RoomImageEntity().copyMembers(roomDto, true);
		insertEntity.setRoomImageId(null);
		RoomImageEntity insertedEntity = this.roomImageRepository.save(insertEntity);
		RoomImageDto resultDto = (RoomImageDto) new RoomImageDto().copyMembers(insertedEntity, true);
		return resultDto;
	}

	public RoomImageDto findById(Long roomId) {
		RoomImageEntity findEntity = this.roomImageRepository.findById(roomId).orElseThrow();
		RoomImageDto resultDto = (RoomImageDto) new RoomImageDto().copyMembers(findEntity, true);
		return resultDto;
	}

	public RoomImageDto update(IRoomImage roomDto) {
		RoomImageDto findDto = this.findById(roomDto.getRoomId());
		findDto.copyMembers(roomDto, false);
		RoomImageEntity updateEntity = (RoomImageEntity) new RoomImageEntity().copyMembers(findDto, true);
		RoomImageEntity updatedEntity = this.roomImageRepository.save(updateEntity);
		RoomImageDto resultDto = (RoomImageDto) new RoomImageDto().copyMembers(updatedEntity, true);
		return resultDto;
	}

	public RoomImageDto deleteById(Long roomId) {
		RoomImageDto findDto = this.findById(roomId);
		this.roomImageRepository.deleteById(roomId);
		return findDto;
	}

	public Page<RoomImageDto> findAllByRoomIdEquals(Long roomId, Pageable pageable) {
		Page<RoomImageEntity> page = this.roomImageRepository.findAllByRoomIdEquals(roomId, pageable);
		List<RoomImageDto> list = this.getListRoomImageDto(page.getContent());
		return new PageImpl<>(list, pageable, page.getTotalElements());
	}

	private List<RoomImageDto> getListRoomImageDto(List<RoomImageEntity> list) {
		List<RoomImageDto> result = list.stream()
				.map( x -> (RoomImageDto)new RoomImageDto().copyMembers(x, true))
				.toList();
		return result;
	}
}
