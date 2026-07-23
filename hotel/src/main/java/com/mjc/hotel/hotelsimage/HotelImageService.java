package com.mjc.hotel.hotelsimage;

import com.mjc.hotel.common.FileUtil;
import com.mjc.hotel.hotels.HotelEntity;
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
public class HotelImageService {

    @Autowired
    private HotelImageRepository hotelImageRepository;

    @Autowired
    private FileUtil fileUtil;

    // 호텔 이미지 파일을 저장소에 업로드하고, DB 저장용 DTO를 만든다.
    public HotelImageResponseDto upload(Long hotelId, MultipartFile file) throws RuntimeException {

        if (hotelId == null || file == null) {
            return null;
        }

        String ext = this.fileUtil.getExtension(file.getOriginalFilename());
        String storeName = this.fileUtil.getRandomStoreFileName(50);

        HotelImageResponseDto hotelImageDto = HotelImageResponseDto.builder()
                .hotelImageId(null)
                .fileName(file.getOriginalFilename())
                .ext(ext)
                .size((int) file.getSize())
                .path(String.valueOf(LocalDateTime.now().getYear()))
                .storeName(storeName)
                .hotelId(hotelId)
                .build();

        if (this.fileUtil.copyFile(file, hotelImageDto.getPath(), hotelImageDto.getStoreName())) {
            return hotelImageDto;
        } else {
            return null;
        }
    }

    // 업로드된 호텔 이미지 정보를 DB에 등록한다.
    public HotelImageResponseDto insert(IHotelImage requestDto) {

        if (requestDto == null) {
            return null;
        }

        HotelImageEntity insertEntity =
                (HotelImageEntity) new HotelImageEntity().copyMembers(requestDto, true);

        insertEntity.setHotelImageId(null);

        HotelImageEntity insertedEntity =
                this.hotelImageRepository.save(insertEntity);

        HotelImageResponseDto resultDto =
                (HotelImageResponseDto) new HotelImageResponseDto().copyMembers(insertedEntity, true);

        return resultDto;
    }

    // 이미지 파일 업로드와 DB 등록을 한 번에 처리한다.
    public HotelImageResponseDto uploadAndInsert(Long hotelId, MultipartFile file) throws RuntimeException {

        HotelImageResponseDto uploadDto = this.upload(hotelId, file);
        HotelImageResponseDto resultDto = this.insert(uploadDto);

        return resultDto;
    }

    // 호텔 이미지 ID로 이미지 정보를 조회한다.
    public HotelImageResponseDto findById(Long hotelImageId) {

        HotelImageEntity findEntity =
                this.hotelImageRepository.findById(hotelImageId).orElseThrow();

        HotelImageResponseDto resultDto =
                (HotelImageResponseDto) new HotelImageResponseDto().copyMembers(findEntity, true);

        return resultDto;
    }

    // 이미지 파일을 Resource 형태로 불러온다.
    public Resource getImageResourceById(HotelImageDto hotelImageDto) throws IOException {

        Resource result = this.fileUtil.loadFileAsResource(
                hotelImageDto.getPath(),
                hotelImageDto.getStoreName());

        return result;
    }

    // 이미지 파일을 byte 배열로 불러온다.
    public byte[] getImageBytesById(HotelImageDto hotelImageDto) throws IOException {

        byte[] result = this.fileUtil.loadFileAsBytes(
                hotelImageDto.getPath(),
                hotelImageDto.getStoreName());

        return result;
    }

    // 호텔 이미지의 DB 정보를 수정한다.
    public HotelImageResponseDto update(IHotelImage requestDto) {

        HotelImageResponseDto findDto = this.findById(requestDto.getHotelImageId());

        findDto.copyMembers(requestDto, false);

        HotelImageEntity updateEntity =
                (HotelImageEntity) new HotelImageEntity().copyMembers(findDto, true);

        HotelImageEntity updatedEntity =
                this.hotelImageRepository.save(updateEntity);

        HotelImageResponseDto resultDto =
                (HotelImageResponseDto) new HotelImageResponseDto().copyMembers(updatedEntity, true);

        return resultDto;
    }

    // 기존 이미지 파일을 삭제한 뒤 새 파일로 교체하고 DB 정보를 수정한다.
    public HotelImageResponseDto uploadAndUpdate(Long hotelImageId, MultipartFile file) throws RuntimeException {

        HotelImageResponseDto findDto = this.findById(hotelImageId);

        this.fileUtil.deleteFile(findDto.getPath(), findDto.getStoreName());

        HotelImageDto upload = this.upload(findDto.getHotelId(), file);

        upload.setHotelImageId(hotelImageId);

        HotelImageEntity updateEntity =
                (HotelImageEntity) new HotelImageEntity().copyMembers(upload, true);

        HotelImageEntity updatedEntity =
                this.hotelImageRepository.save(updateEntity);

        HotelImageResponseDto resultDto =
                (HotelImageResponseDto) new HotelImageResponseDto().copyMembers(updatedEntity, true);

        return resultDto;
    }

    // 이미지 파일과 DB 정보를 함께 삭제하고, 삭제 전 정보를 반환한다.
    public HotelImageDto deleteById(Long hotelImageId) {

        HotelImageDto findDto = this.findById(hotelImageId);

        this.fileUtil.deleteFile(findDto.getPath(), findDto.getStoreName());

        this.hotelImageRepository.deleteById(hotelImageId);

        return findDto;
    }

    // 호텔 ID에 속한 이미지 목록을 페이지 단위로 조회한다.
    public Page<HotelImageResponseDto> findAllByHotelIdEquals(Long hotelId, Pageable pageable) {

        HotelEntity hotel = HotelEntity.builder().hotelId(hotelId).build();

        Page<HotelImageEntity> page =
                this.hotelImageRepository.findAllByHotelEquals(hotel, pageable);

        List<HotelImageResponseDto> list =
                this.getListHotelImageDto(page.getContent());

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    // Entity 목록을 응답용 DTO 목록으로 변환한다.
    private List<HotelImageResponseDto> getListHotelImageDto(List<HotelImageEntity> list) {

        List<HotelImageResponseDto> result = list.stream()
                .map(x -> (HotelImageResponseDto) new HotelImageResponseDto().copyMembers(x, true))
                .toList();

        return result;
    }
}
