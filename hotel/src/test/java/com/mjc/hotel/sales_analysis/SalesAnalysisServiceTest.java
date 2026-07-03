package com.mjc.hotel.sales_analysis;

import com.mjc.hotel.sales_analysis.dto.SalesDashboardResponse;
import com.mjc.hotel.sales_analysis.dto.RoomTypeRevenueDto;
import com.mjc.hotel.sales_analysis.dto.ChannelShareDto;
import com.mjc.hotel.sales_analysis.dto.TopBookingDto;
import com.mjc.hotel.sales_analysis.entity.*;
import com.mjc.hotel.sales_analysis.repository.*;
import com.mjc.hotel.sales_analysis.service.SalesAnalysisService;
import com.mjc.hotel.user.entity.User;
import com.mjc.hotel.user.repository.UserRepository;
import com.mjc.hotel.hotels.HotelEntity;
import com.mjc.hotel.hotels.HotelRepository;
import com.mjc.hotel.hotels.HotelTypeEnum;
import com.mjc.hotel.rooms.dto.RoomEntity;
import com.mjc.hotel.rooms.service.RoomRepository;
import com.mjc.hotel.rooms.enums.RoomStatus;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rooms.enums.RoomViewOption;
import com.mjc.hotel.rooms.enums.RoomBedOption;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class SalesAnalysisServiceTest {

    @Autowired
    private SalesAnalysisService salesAnalysisService;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FakeBookingRepository bookingRepository;

    @Autowired
    private FakePaymentRepository paymentRepository;

    @Autowired
    private EntityManager em;

    private HotelEntity testHotel;
    private RoomEntity standardRoom;
    private RoomEntity deluxeRoom;
    private RoomEntity suiteRoom;
    private User normalUser1;
    private User normalUser2;
    private User vipUser;

    @BeforeEach
    public void setUp() {
        // 기존 데이터와 충돌을 원천 차단하기 위해 9999 범위의 유니크 ID 적용

        // 0. 채널 테이블 초기 적재
        em.createNativeQuery("INSERT INTO channels (채널ID, 채널명, 채널유형, 수수료율, 활성여부) VALUES (1, '야놀자', 'OTA', 10.0, true) " +
                "ON DUPLICATE KEY UPDATE 채널명='야놀자'").executeUpdate();
        em.createNativeQuery("INSERT INTO channels (채널ID, 채널명, 채널유형, 수수료율, 활성여부) VALUES (2, '아고다', 'OTA', 12.0, true) " +
                "ON DUPLICATE KEY UPDATE 채널명='아고다'").executeUpdate();
        em.createNativeQuery("INSERT INTO channels (채널ID, 채널명, 채널유형, 수수료율, 활성여부) VALUES (3, '직접예약', 'DIRECT', 0.0, true) " +
                "ON DUPLICATE KEY UPDATE 채널명='직접예약'").executeUpdate();

        // 1. 테스트 호텔 등록 (Native SQL로 삽입하여 GeneratedValue 충돌 우회)
        em.createNativeQuery("INSERT INTO hotels (hotelId, name, address, city, checkIn, checkOut, isActive, zipCode, phone, email, latitude, longitude, type, created_at, modified_at) " +
                "VALUES (9999, '테스트 MJC 호텔', '서울시 테스트구', '서울', '15:00:00', '11:00:00', true, '12345', '02-1234-5678', 'test@hotel.com', '37.5665', '126.9780', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
                .executeUpdate();

        // 2. 객실 등록 (Native SQL로 삽입)
        em.createNativeQuery("INSERT INTO rooms (room_id, number, room_status, name, size, base_price, max_adult, max_child, is_active, hotel_id, room_type, room_view_option, room_bed_option, floor, created_at, modified_at) " +
                "VALUES (99991, 'T101', 0, '스탠다드 룸', 20, 100000.00, 2, 1, true, 9999, 0, 0, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
                .executeUpdate();

        em.createNativeQuery("INSERT INTO rooms (room_id, number, room_status, name, size, base_price, max_adult, max_child, is_active, hotel_id, room_type, room_view_option, room_bed_option, floor, created_at, modified_at) " +
                "VALUES (99992, 'T102', 0, '디럭스 룸', 30, 150000.00, 2, 1, true, 9999, 2, 0, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
                .executeUpdate();

        em.createNativeQuery("INSERT INTO rooms (room_id, number, room_status, name, size, base_price, max_adult, max_child, is_active, hotel_id, room_type, room_view_option, room_bed_option, floor, created_at, modified_at) " +
                "VALUES (99993, 'T103', 0, '스위트 룸', 50, 300000.00, 4, 2, true, 9999, 1, 3, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
                .executeUpdate();

        // 3. 회원 등록 (Native SQL로 삽입)
        em.createNativeQuery("INSERT INTO users (user_id, email, password, name, phone, role, status, Membership, created_at, modified_at) " +
                "VALUES (999910, 'test_user1_unique@test.com', 'password', '김철수', '010-0000-0000', 'CUSTOMER', 'ACTIVE', 'SILVER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
                .executeUpdate();

        em.createNativeQuery("INSERT INTO users (user_id, email, password, name, phone, role, status, Membership, created_at, modified_at) " +
                "VALUES (999920, 'test_user2_unique@test.com', 'password', '이영희', '010-0000-0000', 'CUSTOMER', 'ACTIVE', 'GOLD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
                .executeUpdate();

        em.createNativeQuery("INSERT INTO users (user_id, email, password, name, phone, role, status, Membership, created_at, modified_at) " +
                "VALUES (999930, 'test_vip_unique@test.com', 'password', '박대표', '010-0000-0000', 'CUSTOMER', 'ACTIVE', 'VIP', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
                .executeUpdate();

        // 영속성 컨텍스트 로딩
        testHotel = hotelRepository.findById(9999L).orElseThrow();
        standardRoom = roomRepository.findById(99991L).orElseThrow();
        deluxeRoom = roomRepository.findById(99992L).orElseThrow();
        suiteRoom = roomRepository.findById(99993L).orElseThrow();
        normalUser1 = userRepository.findById(999910L).orElseThrow();
        normalUser2 = userRepository.findById(999920L).orElseThrow();
        vipUser = userRepository.findById(999930L).orElseThrow();

        // 4. 예약 및 결제 설정 (2026-06 기준 테스트용)

        // 4.1. 5월 예약 1 (김철수 투숙 -> 6월 재방문 여부 체크용 - 야놀자 1L)
        createBookingAndPayment(
                999901L, normalUser1, testHotel, standardRoom, "BT260501", 1,
                LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 11),
                new BigDecimal("100000.00"), LocalDateTime.of(2026, 5, 10, 14, 0), 1L
        );

        // 4.2. 5월 예약 2 (박대표 VIP 투숙 -> 6월 VIP 재방문 여부 체크용 - 아고다 2L)
        createBookingAndPayment(
                999902L, vipUser, testHotel, suiteRoom, "BT260502", 2,
                LocalDate.of(2026, 5, 20), LocalDate.of(2026, 5, 22),
                new BigDecimal("600000.00"), LocalDateTime.of(2026, 5, 20, 12, 0), 2L
        );

        // 4.3. 6월 예약 1 (김철수: 일반 재방문 유저 - 디럭스 150,000원 결제 - 야놀자 1L)
        createBookingAndPayment(
                999903L, normalUser1, testHotel, deluxeRoom, "BT260601", 1,
                LocalDate.of(2026, 6, 5), LocalDate.of(2026, 6, 6),
                new BigDecimal("150000.00"), LocalDateTime.of(2026, 6, 5, 15, 0), 1L
        );

        // 4.4. 6월 예약 2 (박대표: VIP 재방문 유저 - 스위트 900,000원 결제 - 아고다 2L)
        createBookingAndPayment(
                999904L, vipUser, testHotel, suiteRoom, "BT260602", 3,
                LocalDate.of(2026, 6, 28), LocalDate.of(2026, 7, 1),
                new BigDecimal("900000.00"), LocalDateTime.of(2026, 6, 28, 10, 0), 2L
        );

        // 4.5. 6월 예약 3 (이영희: 일반 신규 예약 - 스탠다드 100,000원 결제 - 직접예약 3L)
        createBookingAndPayment(
                999905L, normalUser2, testHotel, standardRoom, "BT260603", 1,
                LocalDate.of(2026, 6, 29), LocalDate.of(2026, 6, 30),
                new BigDecimal("100000.00"), LocalDateTime.of(2026, 6, 29, 11, 30), 3L
        );

        // JPA 컨텍스트 데이터를 DB에 즉시 플러시하여 MyBatis 쿼리가 조회할 수 있도록 조치
        em.flush();
        em.clear();
    }

    private void createBookingAndPayment(
            Long id, User user, HotelEntity hotel, RoomEntity room, String bookingNo, int nights,
            LocalDate checkIn, LocalDate checkOut, BigDecimal amount, LocalDateTime paidAt, Long channelId
    ) {
        FakeBooking booking = FakeBooking.builder()
                .id(id)
                .user(user)
                .hotel(hotel)
                .room(room)
                .channelId(channelId)
                .bookingNo(bookingNo)
                .guestLastName(user.getName().substring(0, 1))
                .guestFirstName(user.getName().substring(1))
                .guestPhone("010-0000-0000")
                .guestEmail(user.getEmail())
                .nationality("KOR")
                .nights(nights)
                .guestCount(2)
                .checkInDate(checkIn)
                .checkInTime(LocalTime.of(15, 0))
                .checkOutDate(checkOut)
                .checkOutTime(LocalTime.of(11, 0))
                .build();

        FakePayment payment = FakePayment.builder()
                .id(id)
                .booking(booking)
                .paymentMethod("CARD")
                .paymentStatus(FakePaymentStatus.PAID)
                .amount(amount)
                .currency("KRW")
                .paidAt(paidAt)
                .build();

        booking.setPayment(payment);
        bookingRepository.save(booking);
        paymentRepository.save(payment);
    }

    @Test
    public void testSalesDashboardStatistics() {
        // Given
        Long hotelId = testHotel.getHotelId();
        String targetMonth = "2026-06";

        // When
        SalesDashboardResponse response = salesAnalysisService.getDashboardData(hotelId, targetMonth);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getHotelId()).isEqualTo(hotelId);
        assertThat(response.getHotelName()).isEqualTo("테스트 MJC 호텔");
        assertThat(response.getYear()).isEqualTo(2026);
        assertThat(response.getMonth()).isEqualTo(6);

        // 1. 이번달 총 매출액 검증 (150,000 + 900,000 + 100,000 = 1,150,000.00)
        assertThat(response.getMetrics().getTotalRevenue().getValue().doubleValue()).isEqualTo(1150000.00);

        // 2. 전월 대비 매출 상승률 검증 (5월 매출 = 100,000 + 600,000 = 700,000.00)
        // 상승률: (1,150,000 - 700,000) / 700,000 * 100 = 64.2857... -> 반올림하여 64.29%
        assertThat(response.getMetrics().getTotalRevenue().getChangeRate()).isEqualTo(64.29);
        assertThat(response.getMetrics().getTotalRevenue().getIsIncreased()).isTrue();

        // 3. 이번달 총 예약 건수 검증 (6월 예약 3건)
        assertThat(response.getMetrics().getBookingCount().getValue()).isEqualTo(3L);
        // 전월(5월) 예약 건수 (2건) -> 예약 상승률: (3 - 2) / 2 * 100 = 50.00%
        assertThat(response.getMetrics().getBookingCount().getChangeRate()).isEqualTo(50.00);

        // 4. 재방문율 검증 (6월 예약자 3명 중 김철수, 박대표 2명이 재방문 -> 66.67%)
        assertThat(response.getMetrics().getReturningGuestRate().getValue()).isEqualTo(66.67);

        // 5. VIP 재방문율 검증 (6월 VIP 예약자 박대표 1명 중 과거 이력 존재 -> 100.00%)
        assertThat(response.getMetrics().getVipReturningGuestRate().getValue()).isEqualTo(100.00);

        // 6. 객실 유형별 통계 개수 검증 (STANDARD: 1개, DELUXE: 1개, SUITE: 1개)
        assertThat(response.getRoomTypeRevenue()).hasSize(3);
        assertThat(response.getRoomTypeRevenue().stream()
                .filter(rt -> rt.getRoomTypeId().equals("SUITE"))
                .findFirst()
                .map(RoomTypeRevenueDto::getRevenue)
                .map(BigDecimal::doubleValue)
                .orElse(0.0)).isEqualTo(900000.00);

        // 7. 채널별 예약 및 매출 비중 검증 (last.txt 요구사항)
        List<ChannelShareDto> channelShares = response.getChannelShares();
        assertThat(channelShares).isNotNull();
        assertThat(channelShares).hasSize(3);

        // 7.1. 아고다(2L) 비중 체크 (매출: 900,000 / 1,150,000 = 78.26%)
        ChannelShareDto agodaShare = channelShares.stream()
                .filter(cs -> cs.getChannelId().equals(2L))
                .findFirst().orElseThrow();
        assertThat(agodaShare.getChannelName()).isEqualTo("아고다");
        assertThat(agodaShare.getBookingCount()).isEqualTo(1L);
        assertThat(agodaShare.getBookingShareRate()).isEqualTo(33.33); // 1/3
        assertThat(agodaShare.getRevenue().doubleValue()).isEqualTo(900000.00);
        assertThat(agodaShare.getRevenueShareRate()).isEqualTo(78.26);

        // 8. 매출 상위 1~5위 예약 목록 검증 (last.txt 요구사항)
        List<TopBookingDto> topBookings = response.getTopBookings();
        assertThat(topBookings).isNotNull();
        assertThat(topBookings).hasSize(3); // 6월 결제 완료 건은 총 3건이므로

        // 8.1. 1위 예약 검증 (박대표, 스위트 룸, 900,000원, 3박)
        TopBookingDto firstPlace = topBookings.get(0);
        assertThat(firstPlace.getRank()).isEqualTo(1);
        assertThat(firstPlace.getGuestName()).isEqualTo("박대표");
        assertThat(firstPlace.getRoomName()).contains("T103"); // T103호 (스위트 룸)
        assertThat(firstPlace.getAmount().doubleValue()).isEqualTo(900000.00);
        assertThat(firstPlace.getNights()).isEqualTo(3);
    }
}
