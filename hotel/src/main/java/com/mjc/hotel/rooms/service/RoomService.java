package com.mjc.hotel.rooms.service;

import com.mjc.hotel.common.FileUtil;
import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.room_images.dto.RoomImageResponseDto;
import com.mjc.hotel.room_images.service.RoomImageService;
import com.mjc.hotel.rooms.dto.IRoom;
import com.mjc.hotel.rooms.dto.RoomDto;
import com.mjc.hotel.rooms.dto.RoomEntity;
import com.mjc.hotel.rooms.dto.RoomResponseWithImagesDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RoomService {
	@Autowired
	private RoomRepository roomRepository;
	@Autowired
	private RoomImageService roomImageService;

	@Autowired
	private FileUtil fileUtil;

	public RoomDto insert(IRoom roomDto) {
		RoomEntity insertEntity = (RoomEntity) new RoomEntity().copyMembers(roomDto, true);
		insertEntity.setRoomId(null);
		RoomEntity insertedEntity = this.roomRepository.save(insertEntity);
		RoomDto resultDto = (RoomDto) new RoomDto().copyMembers(insertedEntity, true);
		return resultDto;
	}

	/**
	 * List<MultipartFile> 첨부파일들을 AttachService insert 를 한다.
	 * 중간에 에러 발생시에 전체 롤백할것인지, 에러난 파일은 건너뛰고 남은 거 계속 저장할것인지 정책으로 결정
	 * 현재는 모두 롤백한다.
	 */
	@Transactional(rollbackFor = {IOException.class}) // 메소드에서 RuntimeException 이 발생하면 롤백처리, Exception 은 롤백 안한다.
	public RoomResponseWithImagesDto insertWithImages(RoomDto roomDto, List<MultipartFile> files) throws IOException {
		if ( roomDto == null ) {
			return null;
		}
		RoomResponseWithImagesDto result = new RoomResponseWithImagesDto();
		RoomDto insertedRoomDto = this.insert(roomDto);
		List<RoomImageResponseDto> roomImageDtos = new ArrayList<>();
		try {
			for (MultipartFile file : files) {
				RoomImageResponseDto insertedRoomImageDto = this.roomImageService.uploadAndInsert(insertedRoomDto.getRoomId(), file);
				roomImageDtos.add(insertedRoomImageDto);
			}
			result.setRoomDto(insertedRoomDto);
			result.setRoomImages(new PageImpl<>(roomImageDtos, Pageable.ofSize(1) , roomImageDtos.size()));
		} catch (RuntimeException e) {
			log.error(e.getMessage());
			roomImageDtos.forEach(
					x -> this.fileUtil.deleteFile(x.getPath(), x.getStoreName())
			);
			throw e;
		}
		return result;
	}

	public RoomDto findById(Long roomId) {
		RoomEntity findEntity = this.roomRepository.findById(roomId).orElseThrow();
		RoomDto resultDto = (RoomDto) new RoomDto().copyMembers(findEntity, true);
		return resultDto;
	}

	public RoomResponseWithImagesDto findByIdWithImages(Long roomId, Pageable pageable) {
		RoomResponseWithImagesDto result = new RoomResponseWithImagesDto();
		RoomEntity findEntity = this.roomRepository.findById(roomId).orElseThrow();
		RoomDto findDto = (RoomDto) new RoomDto().copyMembers(findEntity, true);
		Page<RoomImageResponseDto> page = this.roomImageService.findAllByRoomIdEquals(roomId, pageable);
		result.setRoomDto(findDto);
		result.setRoomImages(page);
		return result;
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
//		Page<RoomEntity> page = this.roomRepository.findAllByHotelIdEquals(hotelId, pageable);
		HotelEntity hotelEntity = HotelEntity.builder().hotelId(hotelId).build();
		Page<RoomEntity> page = this.roomRepository.findAllByHotelEquals(hotelEntity, pageable);
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
