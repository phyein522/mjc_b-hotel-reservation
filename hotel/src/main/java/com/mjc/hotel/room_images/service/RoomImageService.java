package com.mjc.hotel.room_images.service;

import com.mjc.hotel.common.FileUtil;
import com.mjc.hotel.room_images.dto.IRoomImage;
import com.mjc.hotel.room_images.dto.RoomImageDto;
import com.mjc.hotel.room_images.dto.RoomImageEntity;
import com.mjc.hotel.room_images.dto.RoomImageResponseDto;
import com.mjc.hotel.rooms.dto.RoomEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class RoomImageService {
	@Autowired
	private RoomImageRepository roomImageRepository;
	@Autowired
	private FileUtil fileUtil;

	public RoomImageResponseDto upload(Long roomId, MultipartFile file) throws RuntimeException {
		if ( roomId == null || file == null ) {
			return null;
		}
		String ext = this.fileUtil.getExtension(file.getOriginalFilename());
		String storeName = this.fileUtil.getRandomStoreFileName(50);
		RoomImageResponseDto roomImageDto = RoomImageResponseDto.builder().roomImageId(null)
				.fileName(file.getOriginalFilename())
				.ext(ext).size((int) file.getSize()).path(String.valueOf(LocalDateTime.now().getYear()))
				.storeName(storeName).roomId(roomId).build();
		if ( this.fileUtil.copyFile(file, roomImageDto.getPath(), roomImageDto.getStoreName()) ) {
			return roomImageDto;
		} else {
			return null;
		}
	}

	public RoomImageResponseDto insert(IRoomImage requestDto) {
		if ( requestDto == null) {
			return null;
		}
		RoomImageEntity insertEntity = (RoomImageEntity) new RoomImageEntity().copyMembers(requestDto, true);
		insertEntity.setRoomImageId(null);
		RoomImageEntity insertedEntity = this.roomImageRepository.save(insertEntity);
		RoomImageResponseDto resultDto = (RoomImageResponseDto) new RoomImageResponseDto().copyMembers(insertedEntity, true);
		return resultDto;
	}

	public RoomImageResponseDto uploadAndInsert(Long roomId, MultipartFile file) throws RuntimeException {
		RoomImageResponseDto uploadDto = this.upload(roomId, file);
		RoomImageResponseDto resultDto = this.insert(uploadDto);
		return resultDto;
	}

	public RoomImageResponseDto findById(Long roomImageId) {
		RoomImageEntity findEntity = this.roomImageRepository.findById(roomImageId).orElseThrow();
		RoomImageResponseDto resultDto = (RoomImageResponseDto) new RoomImageResponseDto().copyMembers(findEntity, true);
		return resultDto;
	}

	public Resource getImageResourceById(RoomImageDto roomImageDto) throws IOException {
		Resource result = this.fileUtil.loadFileAsResource(roomImageDto.getPath(), roomImageDto.getStoreName());
		return result;
	}

	public byte[] getImageBytesById(RoomImageDto roomImageDto) throws IOException {
		byte[] result = this.fileUtil.loadFileAsBytes(roomImageDto.getPath(), roomImageDto.getStoreName());
		return result;
	}

	public RoomImageResponseDto update(IRoomImage requestDto) {
		RoomImageResponseDto findDto = this.findById(requestDto.getRoomImageId());
		findDto.copyMembers(requestDto, false);
		RoomImageEntity updateEntity = (RoomImageEntity) new RoomImageEntity().copyMembers(findDto, true);
		RoomImageEntity updatedEntity = this.roomImageRepository.save(updateEntity);
		RoomImageResponseDto resultDto = (RoomImageResponseDto) new RoomImageResponseDto().copyMembers(updatedEntity, true);
		return resultDto;
	}

	public RoomImageResponseDto uploadAndUpdate(Long roomImageId, MultipartFile file) throws RuntimeException {
		RoomImageResponseDto findDto = this.findById(roomImageId);
		this.fileUtil.deleteFile(findDto.getPath(), findDto.getStoreName());
		RoomImageDto upload = this.upload(findDto.getRoomId(), file);
		upload.setRoomImageId(roomImageId);
		RoomImageEntity updateEntity = (RoomImageEntity) new RoomImageEntity().copyMembers(upload, true);
		RoomImageEntity updatedEntity = this.roomImageRepository.save(updateEntity);
		RoomImageResponseDto resultDto = (RoomImageResponseDto) new RoomImageResponseDto().copyMembers(updatedEntity, true);
		return resultDto;
	}

	public RoomImageDto deleteById(Long roomId) {
		RoomImageDto findDto = this.findById(roomId);
		this.fileUtil.deleteFile(findDto.getPath(), findDto.getStoreName());
		this.roomImageRepository.deleteById(roomId);
		return findDto;
	}

	public Page<RoomImageResponseDto> findAllByRoomIdEquals(Long roomId, Pageable pageable) {
		RoomEntity room = RoomEntity.builder().roomId(roomId).build();
		Page<RoomImageEntity> page = this.roomImageRepository.findAllByRoomEquals(room, pageable);
		List<RoomImageResponseDto> list = this.getListRoomImageDto(page.getContent());
		return new PageImpl<>(list, pageable, page.getTotalElements());
	}

	private List<RoomImageResponseDto> getListRoomImageDto(List<RoomImageEntity> list) {
		List<RoomImageResponseDto> result = list.stream()
				.map( x -> (RoomImageResponseDto)new RoomImageResponseDto().copyMembers(x, true))
				.toList();
		return result;
	}
}
