package com.mjc.hotel.rates;

import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.hotels.HotelRepository;
import com.mjc.hotel.rates.dto.request.RoomAmenityRequestDto;
import com.mjc.hotel.rates.dto.request.RoomCreateRequestDto;
import com.mjc.hotel.rates.dto.request.RoomUpdateRequestDto;
import com.mjc.hotel.rates.dto.response.RoomDetailResponseDto;
import com.mjc.hotel.rates.dto.response.RoomListResponseDto;
import com.mjc.hotel.rates.repository.RoomRatesRepository;
import com.mjc.hotel.rates.service.RoomAmenityService;
import com.mjc.hotel.rates.service.RoomDetailService;
import com.mjc.hotel.rates.service.RoomRatesImageService;
import com.mjc.hotel.rates.service.RoomRatesService;
import com.mjc.hotel.rooms.dto.RoomEntity;
import com.mjc.hotel.rooms.enums.RoomBedOption;
import com.mjc.hotel.rooms.enums.RoomStatus;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rooms.enums.RoomViewOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoomRatesServiceTest {

    @Mock
    private RoomRatesRepository roomRatesRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private RoomDetailService roomDetailService;

    @Mock
    private RoomAmenityService roomAmenityService;

    @Mock
    private RoomRatesImageService roomRatesImageService;

    @InjectMocks
    private RoomRatesService roomRatesService;

    private HotelEntity mockHotel;
    private RoomEntity mockRoom;

    @BeforeEach
    void setUp() {
        mockHotel = HotelEntity.builder()
                .hotelId(1L)
                .name("MJC Grand Hotel")
                .build();

        mockRoom = RoomEntity.builder()
                .roomId(100L)
                .name("프리미엄 101호")
                .number("101")
                .floor(10)
                .size(45)
                .basePrice(BigDecimal.valueOf(150000))
                .maxAdult(2)
                .maxChild(1)
                .isActive(true)
                .roomType(RoomType.Premium)
                .roomStatus(RoomStatus.EnableReservation)
                .roomViewOption(RoomViewOption.OceanView)
                .roomBedOption(RoomBedOption.QueenBed)
                .hotel(mockHotel)
                .hotelId(1L)
                .build();
    }

    @Test
    @DisplayName("신규 호실 등록 성공")
    void createRoomWithDetails_Success() {
        // given
        RoomCreateRequestDto dto = RoomCreateRequestDto.builder()
                .name("프리미엄 101호")
                .number("101")
                .floor(10)
                .size(45)
                .basePrice(BigDecimal.valueOf(150000))
                .maxAdult(2)
                .maxChild(1)
                .roomType(RoomType.Premium)
                .roomViewOption(RoomViewOption.OceanView)
                .roomBedOption(RoomBedOption.QueenBed)
                .description("바다가 보이는 객실")
                .amenities(RoomAmenityRequestDto.builder().wifi(true).tv(true).build())
                .build();

        given(hotelRepository.findById(1L)).willReturn(Optional.of(mockHotel));
        given(roomRatesRepository.existsByHotelIdAndNumberAndIsActiveTrue(1L, "101")).willReturn(false);
        given(roomRatesRepository.save(any(RoomEntity.class))).willReturn(mockRoom);

        // when
        RoomDetailResponseDto response = roomRatesService.createRoomWithDetails(1L, dto, null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getRoomId()).isEqualTo(100L);
        assertThat(response.getName()).isEqualTo("프리미엄 101호");
        verify(roomDetailService).saveOrUpdateDescription(100L, "바다가 보이는 객실");
        verify(roomAmenityService).saveOrUpdateAmenity(eq(100L), any());
    }

    @Test
    @DisplayName("존재하지 않는 호텔 ID로 호실 등록 시 예외 발생")
    void createRoomWithDetails_HotelNotFound() {
        RoomCreateRequestDto dto = RoomCreateRequestDto.builder().number("101").build();
        given(hotelRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> roomRatesService.createRoomWithDetails(99L, dto, null))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("해당 호텔을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("호실 상세 조회 성공")
    void getRoomDetail_Success() {
        given(roomRatesRepository.findByRoomIdAndIsActiveTrue(100L)).willReturn(Optional.of(mockRoom));
        given(roomRatesImageService.getImagesByRoomId(100L)).willReturn(Collections.emptyList());

        RoomDetailResponseDto response = roomRatesService.getRoomDetail(100L);

        assertThat(response).isNotNull();
        assertThat(response.getRoomId()).isEqualTo(100L);
        assertThat(response.getNumber()).isEqualTo("101");
    }

    @Test
    @DisplayName("호실 목록 조회 (페이징) 성공")
    void getRoomList_Success() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<RoomEntity> roomPage = new PageImpl<>(List.of(mockRoom), pageable, 1);
        given(roomRatesRepository.findByHotel_HotelIdAndIsActiveTrue(1L, pageable)).willReturn(roomPage);

        Page<RoomListResponseDto> result = roomRatesService.getRoomList(1L, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getNumber()).isEqualTo("101");
    }

    @Test
    @DisplayName("호실 수정 성공")
    void updateRoom_Success() {
        RoomUpdateRequestDto updateDto = RoomUpdateRequestDto.builder()
                .name("수정된 객실 이름")
                .basePrice(BigDecimal.valueOf(200000))
                .build();

        given(roomRatesRepository.findByRoomIdAndIsActiveTrue(100L)).willReturn(Optional.of(mockRoom));
        given(roomRatesRepository.save(any(RoomEntity.class))).willReturn(mockRoom);

        RoomDetailResponseDto response = roomRatesService.updateRoom(100L, updateDto);

        assertThat(response).isNotNull();
        verify(roomRatesRepository).save(mockRoom);
    }

    @Test
    @DisplayName("호실 Soft Delete 성공")
    void deleteRoom_Success() {
        given(roomRatesRepository.findByRoomIdAndIsActiveTrue(100L)).willReturn(Optional.of(mockRoom));

        boolean result = roomRatesService.deleteRoom(100L);

        assertThat(result).isTrue();
        assertThat(mockRoom.getIsActive()).isFalse();
        verify(roomRatesRepository).save(mockRoom);
    }
}
