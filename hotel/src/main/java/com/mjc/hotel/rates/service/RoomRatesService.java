package com.mjc.hotel.rates.service;

import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.hotels.HotelRepository;
import com.mjc.hotel.rates.dto.request.RoomCreateRequestDto;
import com.mjc.hotel.rates.dto.request.RoomUpdateRequestDto;
import com.mjc.hotel.rates.dto.response.RoomAmenityResponseDto;
import com.mjc.hotel.rates.dto.response.RoomDetailResponseDto;
import com.mjc.hotel.room_images.dto.RoomImageResponseDto;
import com.mjc.hotel.rates.dto.response.RoomListResponseDto;
import com.mjc.hotel.rates.repository.RoomRatesRepository;
import com.mjc.hotel.rooms.dto.RoomEntity;
import com.mjc.hotel.rooms.enums.RoomStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomRatesService {

    private final RoomRatesRepository roomRatesRepository;
    private final HotelRepository hotelRepository;
    private final RoomDetailService roomDetailService;
    private final RoomAmenityService roomAmenityService;
    private final RoomRatesImageService roomRatesImageService;

    /**
     * 신규 호실, 상세설명, 편의시설 및 객실 이미지 통합 등록
     */
    @Transactional
    public RoomDetailResponseDto createRoomWithDetails(Long hotelId, RoomCreateRequestDto requestDto, List<MultipartFile> files) {
        // 1. 호텔 존재 여부 확인
        HotelEntity hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NoSuchElementException("해당 호텔을 찾을 수 없습니다. ID: " + hotelId));

        // 2. 동일 호텔 내 호실 번호 중복 확인
        if (roomRatesRepository.existsByHotelIdAndNumberAndIsActiveTrue(hotelId, requestDto.getNumber())) {
            throw new IllegalArgumentException("이미 존재하는 호실 번호입니다: " + requestDto.getNumber());
        }

        // 3. RoomEntity 작성 및 저장
        RoomStatus status = (requestDto.getRoomStatus() != null) ? requestDto.getRoomStatus() : RoomStatus.EnableReservation;

        RoomEntity room = RoomEntity.builder()
                .name(requestDto.getName())
                .number(requestDto.getNumber())
                .floor(requestDto.getFloor())
                .size(requestDto.getSize())
                .basePrice(requestDto.getBasePrice())
                .maxAdult(requestDto.getMaxAdult())
                .maxChild(requestDto.getMaxChild())
                .isActive(true)
                .roomType(requestDto.getRoomType())
                .roomStatus(status)
                .roomViewOption(requestDto.getRoomViewOption())
                .roomBedOption(requestDto.getRoomBedOption())
                .hotel(hotel)
                .hotelId(hotelId)
                .build();

        RoomEntity savedRoom = roomRatesRepository.save(room);

        // 4. 상세 설명 (description) 저장
        if (requestDto.getDescription() != null) {
            roomDetailService.saveOrUpdateDescription(savedRoom.getRoomId(), requestDto.getDescription());
        }

        // 5. 편의시설 (amenities) 저장
        if (requestDto.getAmenities() != null) {
            roomAmenityService.saveOrUpdateAmenity(savedRoom.getRoomId(), requestDto.getAmenities());
        }

        // 6. 이미지 저장
        List<RoomImageResponseDto> imageDtos = null;
        if (files != null && !files.isEmpty()) {
            imageDtos = roomRatesImageService.uploadAndInsertMultiple(savedRoom, files);
        } else {
            imageDtos = roomRatesImageService.getImagesByRoomId(savedRoom.getRoomId());
        }

        return buildRoomDetailResponseDto(savedRoom, imageDtos);
    }

    /**
     * 특정 호텔의 호실 목록 조회 (페이징)
     */
    public Page<RoomListResponseDto> getRoomList(Long hotelId, Pageable pageable) {
        Page<RoomEntity> roomPage = roomRatesRepository.findByHotel_HotelIdAndIsActiveTrue(hotelId, pageable);

        return roomPage.map(room -> {
            String description = roomDetailService.getDescriptionByRoomId(room.getRoomId());
            RoomAmenityResponseDto amenities = roomAmenityService.getAmenityResponseByRoomId(room.getRoomId());
            String thumbnailUrl = roomRatesImageService.getThumbnailUrlByRoomId(room.getRoomId());

            return RoomListResponseDto.builder()
                    .roomId(room.getRoomId())
                    .hotelId(hotelId)
                    .name(room.getName())
                    .number(room.getNumber())
                    .floor(room.getFloor())
                    .size(room.getSize())
                    .basePrice(room.getBasePrice())
                    .maxAdult(room.getMaxAdult())
                    .maxChild(room.getMaxChild())
                    .isActive(room.getIsActive())
                    .roomType(room.getRoomType())
                    .roomStatus(room.getRoomStatus())
                    .roomViewOption(room.getRoomViewOption())
                    .roomBedOption(room.getRoomBedOption())
                    .description(description)
                    .amenities(amenities)
                    .thumbnailUrl(thumbnailUrl)
                    .createdAt(room.getCreatedAt())
                    .modifiedAt(room.getModifiedAt())
                    .build();
        });
    }

    /**
     * 호실 상세 정보 조회 (기본정보 + 상세설명 + 편의시설 + 이미지)
     */
    public RoomDetailResponseDto getRoomDetail(Long roomId) {
        RoomEntity room = roomRatesRepository.findByRoomIdAndIsActiveTrue(roomId)
                .orElseThrow(() -> new NoSuchElementException("해당 호실을 찾을 수 없습니다. ID: " + roomId));

        List<RoomImageResponseDto> images = roomRatesImageService.getImagesByRoomId(roomId);
        return buildRoomDetailResponseDto(room, images);
    }

    /**
     * 호실 기본 정보, 상세 설명 및 편의시설 수정
     */
    @Transactional
    public RoomDetailResponseDto updateRoom(Long roomId, RoomUpdateRequestDto requestDto) {
        RoomEntity room = roomRatesRepository.findByRoomIdAndIsActiveTrue(roomId)
                .orElseThrow(() -> new NoSuchElementException("해당 호실을 찾을 수 없습니다. ID: " + roomId));

        if (requestDto.getName() != null) room.setName(requestDto.getName());
        if (requestDto.getNumber() != null) room.setNumber(requestDto.getNumber());
        if (requestDto.getFloor() != null) room.setFloor(requestDto.getFloor());
        if (requestDto.getSize() != null) room.setSize(requestDto.getSize());
        if (requestDto.getBasePrice() != null) room.setBasePrice(requestDto.getBasePrice());
        if (requestDto.getMaxAdult() != null) room.setMaxAdult(requestDto.getMaxAdult());
        if (requestDto.getMaxChild() != null) room.setMaxChild(requestDto.getMaxChild());
        if (requestDto.getIsActive() != null) room.setIsActive(requestDto.getIsActive());
        if (requestDto.getRoomType() != null) room.setRoomType(requestDto.getRoomType());
        if (requestDto.getRoomStatus() != null) room.setRoomStatus(requestDto.getRoomStatus());
        if (requestDto.getRoomViewOption() != null) room.setRoomViewOption(requestDto.getRoomViewOption());
        if (requestDto.getRoomBedOption() != null) room.setRoomBedOption(requestDto.getRoomBedOption());

        RoomEntity updatedRoom = roomRatesRepository.save(room);

        // 상세설명 수정
        if (requestDto.getDescription() != null) {
            roomDetailService.saveOrUpdateDescription(roomId, requestDto.getDescription());
        }

        // 편의시설 수정
        if (requestDto.getAmenities() != null) {
            roomAmenityService.saveOrUpdateAmenity(roomId, requestDto.getAmenities());
        }

        List<RoomImageResponseDto> images = roomRatesImageService.getImagesByRoomId(roomId);
        return buildRoomDetailResponseDto(updatedRoom, images);
    }

    /**
     * 호실 삭제 (Soft Delete: isActive=false)
     */
    @Transactional
    public boolean deleteRoom(Long roomId) {
        RoomEntity room = roomRatesRepository.findByRoomIdAndIsActiveTrue(roomId)
                .orElseThrow(() -> new NoSuchElementException("해당 호실을 찾을 수 없습니다. ID: " + roomId));

        room.setIsActive(false);
        roomRatesRepository.save(room);
        return true;
    }

    /**
     * Response DTO 조립 헬퍼 메소드
     */
    private RoomDetailResponseDto buildRoomDetailResponseDto(RoomEntity room, List<RoomImageResponseDto> images) {
        String description = roomDetailService.getDescriptionByRoomId(room.getRoomId());
        RoomAmenityResponseDto amenities = roomAmenityService.getAmenityResponseByRoomId(room.getRoomId());

        return RoomDetailResponseDto.builder()
                .roomId(room.getRoomId())
                .hotelId(room.getHotel() != null ? room.getHotel().getHotelId() : room.getHotelId())
                .name(room.getName())
                .number(room.getNumber())
                .floor(room.getFloor())
                .size(room.getSize())
                .basePrice(room.getBasePrice())
                .maxAdult(room.getMaxAdult())
                .maxChild(room.getMaxChild())
                .isActive(room.getIsActive())
                .roomType(room.getRoomType())
                .roomStatus(room.getRoomStatus())
                .roomViewOption(room.getRoomViewOption())
                .roomBedOption(room.getRoomBedOption())
                .description(description)
                .amenities(amenities)
                .images(images)
                .createdAt(room.getCreatedAt())
                .modifiedAt(room.getModifiedAt())
                .build();
    }
}
