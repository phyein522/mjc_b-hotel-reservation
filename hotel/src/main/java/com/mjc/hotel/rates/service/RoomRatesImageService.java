package com.mjc.hotel.rates.service;

import com.mjc.hotel.common.FileUtil;
import com.mjc.hotel.common.ServerPortListener;
import com.mjc.hotel.room_images.dto.RoomImageResponseDto;
import com.mjc.hotel.rates.repository.RoomRatesImageRepository;
import com.mjc.hotel.room_images.dto.RoomImageEntity;
import com.mjc.hotel.rooms.dto.RoomEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomRatesImageService {

    private final RoomRatesImageRepository roomRatesImageRepository;
    private final FileUtil fileUtil;

    // 최대 파일 크기 제한 (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 다중 이미지 업로드 및 DB 저장
     */
    @Transactional
    public List<RoomImageResponseDto> uploadAndInsertMultiple(RoomEntity room, List<MultipartFile> files) {
        List<RoomImageResponseDto> result = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return result;
        }

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                RoomImageResponseDto dto = uploadAndInsert(room, file);
                if (dto != null) {
                    result.add(dto);
                }
            }
        }
        return result;
    }

    /**
     * 단일 이미지 파일 업로드 및 DB 저장
     */
    @Transactional
    public RoomImageResponseDto uploadAndInsert(RoomEntity room, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 파일 용량 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("PAYLOAD_TOO_LARGE: 업로드 파일 크기는 최대 5MB를 초과할 수 없습니다.");
        }

        String originalFileName = file.getOriginalFilename();
        String ext = fileUtil.getExtension(originalFileName);
        String storeName = fileUtil.getRandomStoreFileName(20) + (ext.isEmpty() ? "" : "." + ext);
        
        // 저장 경로 생성: uploads/rooms/YYYY/MM
        String subPath = "rooms/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));

        // 로컬 디스크 파일 저장
        boolean isSaved = fileUtil.copyFile(file, subPath, storeName);
        if (!isSaved) {
            log.error("Failed to copy image file to path: {}/{}", subPath, storeName);
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.");
        }

        // DB 메타데이터 저장
        RoomImageEntity entity = RoomImageEntity.builder()
                .fileName(originalFileName)
                .storeName(storeName)
                .path(subPath)
                .ext(ext)
                .size((int) file.getSize())
                .room(room)
                .build();

        RoomImageEntity savedEntity = roomRatesImageRepository.save(entity);
        return convertToResponseDto(savedEntity);
    }

    /**
     * 특정 호실의 모든 이미지 목록 조회
     */
    public List<RoomImageResponseDto> getImagesByRoomId(Long roomId) {
        if (roomId == null) {
            return new ArrayList<>();
        }
        return roomRatesImageRepository.findByRoom_RoomId(roomId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 호실의 대표 썸네일 URL 조회 (첫번째 이미지)
     */
    public String getThumbnailUrlByRoomId(Long roomId) {
        List<RoomImageEntity> images = roomRatesImageRepository.findByRoom_RoomId(roomId);
        if (images != null && !images.isEmpty()) {
            return convertToResponseDto(images.get(0)).getImageUrl();
        }
        return null;
    }

    /**
     * 객실 이미지 개별 삭제
     */
    @Transactional
    public boolean deleteImage(Long roomId, Long imageId) {
        Optional<RoomImageEntity> imageOpt = roomRatesImageRepository.findByRoomImageIdAndRoom_RoomId(imageId, roomId);
        if (imageOpt.isPresent()) {
            RoomImageEntity entity = imageOpt.get();
            // 디스크 파일 삭제
            try {
                fileUtil.deleteFile(entity.getPath(), entity.getStoreName());
            } catch (Exception e) {
                log.warn("Could not delete file from disk: {}/{}", entity.getPath(), entity.getStoreName(), e);
            }
            // DB 레코드 삭제
            roomRatesImageRepository.delete(entity);
            return true;
        }
        return false;
    }

    /**
     * Entity -> Response DTO 변환
     */
    public RoomImageResponseDto convertToResponseDto(RoomImageEntity entity) {
        if (entity == null) return null;
        String imageUrl = String.format("http://localhost:%d/api/roomimage/image/%d",
                ServerPortListener.PORT,
                entity.getRoomImageId());

        return RoomImageResponseDto.builder()
                .roomImageId(entity.getRoomImageId())
                .fileName(entity.getFileName())
                .storeName(entity.getStoreName())
                .path(entity.getPath())
                .ext(entity.getExt())
                .size(entity.getSize())
                .imageUrl(imageUrl)
                .build();
    }
}
