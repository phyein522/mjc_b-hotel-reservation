package com.mjc.hotel.bookings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * BookingRestController 의 모든 URL Mapping 메소드에 대한 단위 테스트
 *
 * @WebMvcTest 를 사용하여 웹 계층만 슬라이스 테스트하고,
 * BookingService 는 @MockitoBean 으로 목(mock) 처리한다.
 *
 * 테스트 대상 URL
 * - POST  /api/bookings/insert
 * - GET   /api/bookings/{userId}
 * - PATCH /api/bookings/cancel/{bookingId}
 *
 * ApiResponse 의 실제 페이로드 필드명은 "responseData" 이다.
 *
 * 예:
 * {
 *   "responseCode": "INSERT_OK",
 *   "message": "booking insert ok",
 *   "responseData": { ... }
 * }
 */
@WebMvcTest(BookingRestController.class)
public class TestBookingRestController {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDto sampleBookingDto;

    @BeforeEach
    void setUp() {
        sampleBookingDto = new BookingDto();

        sampleBookingDto.setBookingId(1L);
        sampleBookingDto.setBookingNo("SN-2026-0713-1234");

        sampleBookingDto.setGuestName("홍길동");
        sampleBookingDto.setNationality(Nationality.KOREA);
        sampleBookingDto.setGuestPhone("010-1234-5678");
        sampleBookingDto.setGuestEmail("hong@test.com");
        sampleBookingDto.setSpecialRequest("고층 객실 요청");

        sampleBookingDto.setNights(2);
        sampleBookingDto.setAdultCount(2);
        sampleBookingDto.setChildCount(1);

        sampleBookingDto.setCheckinDate(
                LocalDate.of(2026, 7, 20)
        );
        sampleBookingDto.setCheckoutDate(
                LocalDate.of(2026, 7, 22)
        );

        sampleBookingDto.setCheckinTime(
                LocalTime.of(15, 0)
        );
        sampleBookingDto.setCheckoutTime(
                LocalTime.of(11, 0)
        );

        sampleBookingDto.setCancelledAt(null);

        sampleBookingDto.setUserId(10L);
        sampleBookingDto.setRoomId(100L);
    }

    @Test
    @DisplayName("POST /api/bookings/insert - 예약 등록 성공")
    void insert_shouldReturnOk() throws Exception {
        // given
        when(bookingService.insert(any(BookingDto.class)))
                .thenReturn(sampleBookingDto);

        // when & then
        mockMvc.perform(
                        post("/api/bookings/insert")
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(
                                                sampleBookingDto
                                        )
                                )
                )
                .andExpect(status().isOk())

                .andExpect(
                        jsonPath("$.responseCode")
                                .value("INSERT_OK")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("booking insert ok")
                )

                .andExpect(
                        jsonPath("$.responseData.bookingId")
                                .value(sampleBookingDto.getBookingId())
                )
                .andExpect(
                        jsonPath("$.responseData.bookingNo")
                                .value(sampleBookingDto.getBookingNo())
                )

                .andExpect(
                        jsonPath("$.responseData.guestName")
                                .value(sampleBookingDto.getGuestName())
                )
                .andExpect(
                        jsonPath("$.responseData.nationality")
                                .value(
                                        sampleBookingDto
                                                .getNationality()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.responseData.guestPhone")
                                .value(sampleBookingDto.getGuestPhone())
                )
                .andExpect(
                        jsonPath("$.responseData.guestEmail")
                                .value(sampleBookingDto.getGuestEmail())
                )
                .andExpect(
                        jsonPath("$.responseData.specialRequest")
                                .value(sampleBookingDto.getSpecialRequest())
                )

                .andExpect(
                        jsonPath("$.responseData.nights")
                                .value(sampleBookingDto.getNights())
                )
                .andExpect(
                        jsonPath("$.responseData.adultCount")
                                .value(sampleBookingDto.getAdultCount())
                )
                .andExpect(
                        jsonPath("$.responseData.childCount")
                                .value(sampleBookingDto.getChildCount())
                )

                .andExpect(
                        jsonPath("$.responseData.checkinDate")
                                .value(
                                        sampleBookingDto
                                                .getCheckinDate()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.responseData.checkoutDate")
                                .value(
                                        sampleBookingDto
                                                .getCheckoutDate()
                                                .toString()
                                )
                )

                /*
                 * LocalTime 의 JSON 문자열 형식은 Jackson 설정에 따라
                 * "15:00", "15:00:00" 등으로 달라질 수 있다.
                 *
                 * 따라서 정확한 문자열보다는 값의 존재 여부를 확인한다.
                 */
                .andExpect(
                        jsonPath("$.responseData.checkinTime")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.responseData.checkoutTime")
                                .exists()
                )

                /*
                 * 예약 생성 시 cancelledAt은 null이다.
                 */
                .andExpect(
                        jsonPath("$.responseData.cancelledAt")
                                .doesNotExist()
                )

                .andExpect(
                        jsonPath("$.responseData.userId")
                                .value(sampleBookingDto.getUserId())
                )
                .andExpect(
                        jsonPath("$.responseData.roomId")
                                .value(sampleBookingDto.getRoomId())
                );

        verify(
                bookingService,
                times(1)
        ).insert(any(BookingDto.class));
    }

    @Test
    @DisplayName("GET /api/bookings/{userId} - 사용자별 예약 목록 조회 성공")
    void findAllByUserId_shouldReturnOk() throws Exception {
        // given
        Long userId = 10L;

        /*
         * BookingResponseDto는 다음 데이터를 포함하는 복합 응답 DTO이다.
         *
         * - BookingDto
         * - RoomImageResponseDto 목록
         * - HotelImageResponseDto 목록
         *
         * 이 테스트는 BookingService가 반환한 리스트를
         * BookingRestController가 ApiResponse로 정상 반환하는지
         * 확인하는 컨트롤러 단위 테스트이다.
         *
         * 따라서 여기서는 빈 리스트를 사용한다.
         */
        List<BookingResponseDto> responseList = List.of();

        when(bookingService.findAllByUserId(userId))
                .thenReturn(responseList);

        // when & then
        mockMvc.perform(
                        get(
                                "/api/bookings/{userId}",
                                userId
                        )
                )
                .andExpect(status().isOk())

                .andExpect(
                        jsonPath("$.responseCode")
                                .value("SELECT_OK")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("bookings select ok")
                )
                .andExpect(
                        jsonPath("$.responseData")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$.responseData")
                                .isEmpty()
                );

        verify(
                bookingService,
                times(1)
        ).findAllByUserId(userId);
    }

    @Test
    @DisplayName("PATCH /api/bookings/cancel/{bookingId} - 예약 취소 성공")
    void cancel_shouldReturnOk() throws Exception {
        // given
        Long bookingId = 1L;

        BookingDto cancelBookingDto = new BookingDto();

        cancelBookingDto.setBookingId(
                sampleBookingDto.getBookingId()
        );
        cancelBookingDto.setBookingNo(
                sampleBookingDto.getBookingNo()
        );

        cancelBookingDto.setGuestName(
                sampleBookingDto.getGuestName()
        );
        cancelBookingDto.setNationality(
                sampleBookingDto.getNationality()
        );
        cancelBookingDto.setGuestPhone(
                sampleBookingDto.getGuestPhone()
        );
        cancelBookingDto.setGuestEmail(
                sampleBookingDto.getGuestEmail()
        );
        cancelBookingDto.setSpecialRequest(
                sampleBookingDto.getSpecialRequest()
        );

        cancelBookingDto.setNights(
                sampleBookingDto.getNights()
        );
        cancelBookingDto.setAdultCount(
                sampleBookingDto.getAdultCount()
        );
        cancelBookingDto.setChildCount(
                sampleBookingDto.getChildCount()
        );

        cancelBookingDto.setCheckinDate(
                sampleBookingDto.getCheckinDate()
        );
        cancelBookingDto.setCheckoutDate(
                sampleBookingDto.getCheckoutDate()
        );
        cancelBookingDto.setCheckinTime(
                sampleBookingDto.getCheckinTime()
        );
        cancelBookingDto.setCheckoutTime(
                sampleBookingDto.getCheckoutTime()
        );

        cancelBookingDto.setCancelledAt(
                LocalDateTime.of(
                        2026,
                        7,
                        15,
                        10,
                        30
                )
        );

        cancelBookingDto.setUserId(
                sampleBookingDto.getUserId()
        );
        cancelBookingDto.setRoomId(
                sampleBookingDto.getRoomId()
        );

        when(bookingService.cancel(bookingId))
                .thenReturn(cancelBookingDto);

        // when & then
        mockMvc.perform(
                        patch(
                                "/api/bookings/cancel/{bookingId}",
                                bookingId
                        )
                )
                .andExpect(status().isOk())

                .andExpect(
                        jsonPath("$.responseCode")
                                .value("UPDATE_OK")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("booking cancel ok")
                )

                .andExpect(
                        jsonPath("$.responseData.bookingId")
                                .value(cancelBookingDto.getBookingId())
                )
                .andExpect(
                        jsonPath("$.responseData.bookingNo")
                                .value(cancelBookingDto.getBookingNo())
                )

                .andExpect(
                        jsonPath("$.responseData.guestName")
                                .value(cancelBookingDto.getGuestName())
                )
                .andExpect(
                        jsonPath("$.responseData.nationality")
                                .value(
                                        cancelBookingDto
                                                .getNationality()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.responseData.guestPhone")
                                .value(cancelBookingDto.getGuestPhone())
                )
                .andExpect(
                        jsonPath("$.responseData.guestEmail")
                                .value(cancelBookingDto.getGuestEmail())
                )
                .andExpect(
                        jsonPath("$.responseData.specialRequest")
                                .value(cancelBookingDto.getSpecialRequest())
                )

                .andExpect(
                        jsonPath("$.responseData.nights")
                                .value(cancelBookingDto.getNights())
                )
                .andExpect(
                        jsonPath("$.responseData.adultCount")
                                .value(cancelBookingDto.getAdultCount())
                )
                .andExpect(
                        jsonPath("$.responseData.childCount")
                                .value(cancelBookingDto.getChildCount())
                )

                .andExpect(
                        jsonPath("$.responseData.checkinDate")
                                .value(
                                        cancelBookingDto
                                                .getCheckinDate()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.responseData.checkoutDate")
                                .value(
                                        cancelBookingDto
                                                .getCheckoutDate()
                                                .toString()
                                )
                )

                .andExpect(
                        jsonPath("$.responseData.checkinTime")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.responseData.checkoutTime")
                                .exists()
                )

                /*
                 * 예약 취소 결과이므로 cancelledAt 값이 존재해야 한다.
                 */
                .andExpect(
                        jsonPath("$.responseData.cancelledAt")
                                .exists()
                )

                .andExpect(
                        jsonPath("$.responseData.userId")
                                .value(cancelBookingDto.getUserId())
                )
                .andExpect(
                        jsonPath("$.responseData.roomId")
                                .value(cancelBookingDto.getRoomId())
                );

        verify(
                bookingService,
                times(1)
        ).cancel(bookingId);
    }
}