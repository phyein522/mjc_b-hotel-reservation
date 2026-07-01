package com.mjc.hotel.rooms.service;

import com.mjc.hotel.rooms.dto.IRoom;
import com.mjc.hotel.rooms.dto.RoomDto;
import com.mjc.hotel.rooms.dto.RoomEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
	@Autowired
	private RoomRepository roomRepository;

	public RoomDto insert(IRoom roomDto) {
		RoomEntity insertEntity = (RoomEntity) new RoomEntity().copyMembers(roomDto, true);
		insertEntity.setRoomId(null);
		RoomEntity insertedEntity = this.roomRepository.save(insertEntity);
		RoomDto resultDto = (RoomDto) new RoomDto().copyMembers(insertedEntity, true);
		return resultDto;
	}

	public RoomDto findById(Long roomId) {
		RoomEntity findEntity = this.roomRepository.findById(roomId).orElseThrow();
		RoomDto resultDto = (RoomDto) new RoomDto().copyMembers(findEntity, true);
		return resultDto;
	}

	public RoomDto update(IRoom roomDto) {
		RoomDto findDto = this.findById(roomDto.getRoomId());
		findDto.copyMembers(roomDto, false);
		RoomEntity updateEntity = (RoomEntity) new RoomEntity().copyMembers(findDto, true);
		RoomEntity updatedEntity = this.roomRepository.save(updateEntity);
		RoomDto resultDto = (RoomDto) new RoomDto().copyMembers(updatedEntity, true);
		return resultDto;
	}

	public RoomDto deleteById(Long roomId) {
		RoomDto findDto = this.findById(roomId);
		this.roomRepository.deleteById(roomId);
		return findDto;
	}

	public Page<RoomDto> findAllByHotelIdEquals(Long hotelId, Pageable pageable) {
		Page<RoomEntity> page = this.roomRepository.findAllByHotelIdEquals(hotelId, pageable);
		List<RoomDto> list = this.getListRoomDto(page.getContent());
		return new PageImpl<>(list, pageable, page.getTotalElements());
	}

	private List<RoomDto> getListRoomDto(List<RoomEntity> list) {
		List<RoomDto> result = list.stream()
				.map( x -> (RoomDto)new RoomDto().copyMembers(x, true))
				.toList();
		return result;
	}
}
