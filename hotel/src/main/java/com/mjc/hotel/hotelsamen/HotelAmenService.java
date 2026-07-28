package com.mjc.hotel.hotelsamen;

import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.hotels.HotelRepository;
import com.mjc.hotel.user.entity.Role;
import com.mjc.hotel.user.entity.UserEntity;
import com.mjc.hotel.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HotelAmenService {
    @Autowired
    private HotelAmenRepository hotelAmenRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    // Entity 목록을 응답용 DTO 목록으로 변환한다.
    private List<HotelAmenDto> getListHotelAmenDto(List<HotelAmenEntity> list) {
        return list.stream()
                .map(x -> (HotelAmenDto) new HotelAmenDto().copyMembers(x, true))
                .toList();
    }

    // 편의시설 ID로 호텔 편의시설 정보를 조회한다.
    public HotelAmenDto findById(Long amenId) {
        HotelAmenEntity entity = hotelAmenRepository.findById(amenId)
                .orElseThrow(() -> new IllegalArgumentException("호텔 편의시설을 찾을 수 없습니다."));
        return (HotelAmenDto) new HotelAmenDto().copyMembers(entity, true);
    }

    // 호텔 편의시설 정보를 등록한다.
    public HotelAmenDto insert(IHotelAmen dto, Long userId) {
        HotelAmenEntity entity = (HotelAmenEntity) new HotelAmenEntity().copyMembers(dto, true);
        validateHotelManager(userId, entity.getHotelId());
        entity.setAmenId(null);
        HotelAmenEntity inserted = hotelAmenRepository.save(entity);
        return (HotelAmenDto) new HotelAmenDto().copyMembers(inserted, true);
    }

    // 기존 호텔 편의시설 정보를 수정한다.
    public HotelAmenDto update(IHotelAmen dto, Long userId) {
        HotelAmenDto findDto = this.findById(dto.getAmenId());
        findDto.copyMembers(dto, false);
        HotelAmenEntity entity =
                (HotelAmenEntity) new HotelAmenEntity().copyMembers(findDto, true);
        validateHotelManager(userId, entity.getHotelId());
        HotelAmenEntity updated = hotelAmenRepository.save(entity);
        return (HotelAmenDto) new HotelAmenDto().copyMembers(updated, true);
    }

    // 편의시설 ID로 삭제하고, 삭제 전 정보를 반환한다.
    public HotelAmenDto deleteById(Long amenId, Long userId) {
        HotelAmenDto findDto = this.findById(amenId);
        validateHotelManager(userId, findDto.getHotelId());
        hotelAmenRepository.deleteById(amenId);
        return findDto;
    }

    // 호텔 ID에 속한 편의시설 목록을 페이지 단위로 조회한다.
    public Page<HotelAmenDto> findAllByHotelIdEquals(Long hotelId, Pageable pageable) {
        HotelEntity hotel = HotelEntity.builder().hotelId(hotelId).build();
        Page<HotelAmenEntity> page = this.hotelAmenRepository.findAllByHotelEquals(hotel, pageable);
        List<HotelAmenDto> list = this.getListHotelAmenDto(page.getContent());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    // 관리자 또는 해당 호텔 매니저만 호텔 편의시설을 관리할 수 있게 검증한다.
    private void validateHotelManager(Long userId, Long hotelId) {
        if (userId == null) {
            throw new IllegalArgumentException("호텔 편의시설을 관리할 userId가 필요합니다.");
        }
        if (hotelId == null) {
            throw new IllegalArgumentException("호텔 편의시설을 등록할 hotelId가 필요합니다.");
        }

        UserEntity user = this.userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPER_ADMIN) {
            return;
        }

        if (user.getRole() != Role.HOTEL_MANAGER) {
            throw new IllegalArgumentException("호텔 관리자만 호텔 편의시설을 관리할 수 있습니다.");
        }

        Long hotelManagerUserId = this.hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다."))
                .getUserId();

        if (!hotelManagerUserId.equals(userId)) {
            throw new IllegalArgumentException("자신이 관리하는 호텔의 편의시설만 관리할 수 있습니다.");
        }
    }
}
