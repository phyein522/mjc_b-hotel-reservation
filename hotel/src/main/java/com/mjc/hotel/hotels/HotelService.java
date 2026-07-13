package com.mjc.hotel.hotels;

import com.mjc.hotel.common.FileUtil;
import com.mjc.hotel.hotelsimage.HotelImageResponseDto;
import com.mjc.hotel.hotelsimage.HotelImageService;
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
    private FileUtil fileUtil;

    public HotelDto insert(IHotel hotelDto) {
        HotelEntity insertEntity = (HotelEntity) new HotelEntity().copyMembers(hotelDto, true);
        insertEntity.setHotelId(null);
        HotelEntity insertedEntity = this.hotelRepository.save(insertEntity);
        HotelDto resultDto = (HotelDto) new HotelDto().copyMembers(insertedEntity, true);
        return resultDto;
    }

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

    public Page<HotelDto> findAll(Pageable pageable) {
        Page<HotelEntity> page = hotelRepository.findAll(pageable);
        List<HotelDto> list = getListHotelDto(page.getContent());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public HotelDto findById(Long hotelId) {
        HotelEntity findEntity = this.hotelRepository.findById(hotelId).orElseThrow();
        HotelDto resultDto = (HotelDto) new HotelDto().copyMembers(findEntity, true);
        return resultDto;
    }

    public HotelResponseWithImagesDto findByIdWithImages(Long hotelId, Pageable pageable) {
        HotelResponseWithImagesDto result = new HotelResponseWithImagesDto();
        HotelEntity findEntity = this.hotelRepository.findById(hotelId).orElseThrow();
        HotelDto findDto = (HotelDto) new HotelDto().copyMembers(findEntity, true);
        Page<HotelImageResponseDto> page = this.hotelImageService.findAllByHotelIdEquals(hotelId, pageable);
        result.setHotelDto(findDto);
        result.setHotelImages(page);
        return result;
    }


    public HotelDto update(IHotel hotelDto) {
        HotelDto findDto = this.findById(hotelDto.getHotelId());
        findDto.copyMembers(hotelDto, false);
        HotelEntity updateEntity = (HotelEntity) new HotelEntity().copyMembers(findDto, true);
        HotelEntity updatedEntity = this.hotelRepository.save(updateEntity);
        HotelDto resultDto = (HotelDto) new HotelDto().copyMembers(updatedEntity, true);
        return resultDto;
    }

    public HotelDto deleteById(Long hotelId) {
        HotelDto findDto = this.findById(hotelId);
        this.hotelRepository.deleteById(hotelId);
        return findDto;
    }

    private List<HotelDto> getListHotelDto(List<HotelEntity> list) {
        return list.stream()
                .map(x -> (HotelDto) new HotelDto().copyMembers(x, true))
                .toList();
    }
}
