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

    public HotelImageResponseDto uploadAndInsert(Long hotelId, MultipartFile file) throws RuntimeException {

        HotelImageResponseDto uploadDto = this.upload(hotelId, file);
        HotelImageResponseDto resultDto = this.insert(uploadDto);

        return resultDto;
    }

    public HotelImageResponseDto findById(Long hotelImageId) {

        HotelImageEntity findEntity =
                this.hotelImageRepository.findById(hotelImageId).orElseThrow();

        HotelImageResponseDto resultDto =
                (HotelImageResponseDto) new HotelImageResponseDto().copyMembers(findEntity, true);

        return resultDto;
    }

    public Resource getImageResourceById(HotelImageDto hotelImageDto) throws IOException {

        Resource result = this.fileUtil.loadFileAsResource(
                hotelImageDto.getPath(),
                hotelImageDto.getStoreName());

        return result;
    }

    public byte[] getImageBytesById(HotelImageDto hotelImageDto) throws IOException {

        byte[] result = this.fileUtil.loadFileAsBytes(
                hotelImageDto.getPath(),
                hotelImageDto.getStoreName());

        return result;
    }

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

    public HotelImageDto deleteById(Long hotelImageId) {

        HotelImageDto findDto = this.findById(hotelImageId);

        this.fileUtil.deleteFile(findDto.getPath(), findDto.getStoreName());

        this.hotelImageRepository.deleteById(hotelImageId);

        return findDto;
    }

    public Page<HotelImageResponseDto> findAllByHotelIdEquals(Long hotelId, Pageable pageable) {

        HotelEntity hotel = HotelEntity.builder().hotelId(hotelId).build();

        Page<HotelImageEntity> page =
                this.hotelImageRepository.findAllByHotelEquals(hotel, pageable);

        List<HotelImageResponseDto> list =
                this.getListHotelImageDto(page.getContent());

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    private List<HotelImageResponseDto> getListHotelImageDto(List<HotelImageEntity> list) {

        List<HotelImageResponseDto> result = list.stream()
                .map(x -> (HotelImageResponseDto) new HotelImageResponseDto().copyMembers(x, true))
                .toList();

        return result;
    }
}