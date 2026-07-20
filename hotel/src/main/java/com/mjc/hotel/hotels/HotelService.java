package com.mjc.hotel.hotels;

import com.mjc.hotel.common.FileUtil;
import com.mjc.hotel.hotelsimage.HotelImageResponseDto;
import com.mjc.hotel.hotelsimage.HotelImageService;
import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.UserEntity;
import com.mjc.hotel.user.repository.UserRepository;
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
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HotelImageService hotelImageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileUtil fileUtil;

    // 호텔 기본 정보를 등록하고, 등록 사용자가 호텔 매니저인지 확인한다.
    public HotelDto insert(IHotel hotelDto) {
        HotelEntity insertEntity = (HotelEntity) new HotelEntity().copyMembers(hotelDto, true);
        validateHotelManager(insertEntity);
        insertEntity.setHotelId(null);
        HotelEntity insertedEntity = this.hotelRepository.save(insertEntity);
        HotelDto resultDto = (HotelDto) new HotelDto().copyMembers(insertedEntity, true);
        return resultDto;
    }

    // 호텔 정보와 이미지 파일들을 함께 등록한다. 중간 실패 시 업로드된 파일은 삭제한다.
    @Transactional(rollbackFor = {IOException.class})
    public HotelResponseWithImagesDto insertWithImages(HotelDto hotelDto, List<MultipartFile> files) throws IOException {
        if ( hotelDto == null ) {
            return null;
        }
        HotelResponseWithImagesDto result = new HotelResponseWithImagesDto();
        HotelDto insertedHotelDto = this.insert(hotelDto);
        List<HotelImageResponseDto> hotelImageDtos = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                HotelImageResponseDto insertedHotelImageDto = this.hotelImageService.uploadAndInsert(insertedHotelDto.getHotelId(), file);
                hotelImageDtos.add(insertedHotelImageDto);
            }
            result.setHotelDto(insertedHotelDto);
            result.setHotelImages(new PageImpl<>(hotelImageDtos, Pageable.ofSize(1) , hotelImageDtos.size()));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            hotelImageDtos.forEach(
                    x -> this.fileUtil.deleteFile(x.getPath(), x.getStoreName())
            );
            throw e;
        }
        return result;
    }

    // 호텔 목록을 페이지 단위로 조회한다.
    public Page<HotelDto> findAll(Pageable pageable) {
        Page<HotelEntity> page = hotelRepository.findAll(pageable);
        List<HotelDto> list = getListHotelDto(page.getContent());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    // 호텔 ID로 호텔 기본 정보를 조회한다.
    public HotelDto findById(Long hotelId) {
        HotelEntity findEntity = this.hotelRepository.findById(hotelId).orElseThrow();
        HotelDto resultDto = (HotelDto) new HotelDto().copyMembers(findEntity, true);
        return resultDto;
    }

    // 호텔 기본 정보와 해당 호텔의 이미지 목록을 함께 조회한다.
    public HotelResponseWithImagesDto findByIdWithImages(Long hotelId, Pageable pageable) {
        HotelResponseWithImagesDto result = new HotelResponseWithImagesDto();
        HotelEntity findEntity = this.hotelRepository.findById(hotelId).orElseThrow();
        HotelDto findDto = (HotelDto) new HotelDto().copyMembers(findEntity, true);
        Page<HotelImageResponseDto> page = this.hotelImageService.findAllByHotelIdEquals(hotelId, pageable);
        result.setHotelDto(findDto);
        result.setHotelImages(page);
        return result;
    }


    // 기존 호텔 정보를 수정하고, 수정 사용자가 호텔 매니저인지 확인한다.
    public HotelDto update(IHotel hotelDto) {
        HotelDto findDto = this.findById(hotelDto.getHotelId());
        findDto.copyMembers(hotelDto, false);
        HotelEntity updateEntity = (HotelEntity) new HotelEntity().copyMembers(findDto, true);
        validateHotelManager(updateEntity);
        HotelEntity updatedEntity = this.hotelRepository.save(updateEntity);
        HotelDto resultDto = (HotelDto) new HotelDto().copyMembers(updatedEntity, true);
        return resultDto;
    }

    // 호텔 ID로 호텔을 삭제하고, 삭제 전 정보를 반환한다.
    public HotelDto deleteById(Long hotelId) {
        HotelDto findDto = this.findById(hotelId);
        this.hotelRepository.deleteById(hotelId);
        return findDto;
    }

    // Entity 목록을 응답용 DTO 목록으로 변환한다.
    private List<HotelDto> getListHotelDto(List<HotelEntity> list) {
        return list.stream()
                .map(x -> (HotelDto) new HotelDto().copyMembers(x, true))
                .toList();
    }

    // 호텔 등록/수정 요청자가 HOTEL_MANAGER 권한인지 검증한다.
    private void validateHotelManager(IHotel hotelDto) {
        if (hotelDto.getUserId() == null) {
            throw new IllegalArgumentException("호텔 관리자 userId가 필요합니다.");
        }

        UserEntity user = this.userRepository.findById(hotelDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("호텔 관리자를 찾을 수 없습니다."));

        if (user.getRole() != Role.HOTEL_MANAGER) {
            throw new IllegalArgumentException("HOTEL_MANAGER 권한의 사용자만 호텔을 관리할 수 있습니다.");
        }

        hotelDto.setUser(user);
    }
}
