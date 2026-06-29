package com.mjc.hotel.sales_analysis;

import com.mjc.hotel.sales_analysis.dto.SalesDashboardResponse;
import com.mjc.hotel.sales_analysis.dto.RoomTypeRevenueDto;
import com.mjc.hotel.sales_analysis.entity.Booking;
import com.mjc.hotel.sales_analysis.entity.Hotel;
import com.mjc.hotel.sales_analysis.entity.Payment;
import com.mjc.hotel.sales_analysis.entity.PaymentStatus;
import com.mjc.hotel.sales_analysis.entity.Room;
import com.mjc.hotel.sales_analysis.repository.BookingRepository;
import com.mjc.hotel.sales_analysis.repository.HotelRepository;
import com.mjc.hotel.sales_analysis.repository.PaymentRepository;
import com.mjc.hotel.sales_analysis.repository.RoomRepository;
import com.mjc.hotel.sales_analysis.service.SalesAnalysisService;
import com.mjc.hotel.user.entity.User;
import com.mjc.hotel.user.repository.UserRepository;
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
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EntityManager em;

    private Hotel testHotel;
    private Room standardRoom;
    private Room deluxeRoom;
    private Room suiteRoom;
    private User normalUser1;
    private User normalUser2;
    private User vipUser;

    @BeforeEach
    public void setUp() {
        // 기존 데이터와 충돌을 원천 차단하기 위해 9999 범위의 유니크 ID 적용
        
        // 1. 테스트 호텔 등록
        testHotel = Hotel.builder()
                .id(9999L)
                .name("테스트 MJC 호텔")
                .address("서울시 테스트구")
                .city("서울")
                .checkInTime(LocalTime.of(15, 0))
                .checkOutTime(LocalTime.of(11, 0))
                .isActive(true)
                .build();
        hotelRepository.save(testHotel);

        // 2. 객실 등록
        standardRoom = Room.builder()
                .id(99991L)
                .roomNumber("T101")
                .status("AVAILABLE")
                .name("스탠다드 룸")
                .basePrice(new BigDecimal("100000.00"))
                .isActive(true)
                .hotel(testHotel)
                .roomTypeId("STANDARD")
                .roomKindId("NORMAL")
                .build();
        roomRepository.save(standardRoom);

        deluxeRoom = Room.builder()
                .id(99992L)
                .roomNumber("T102")
                .status("AVAILABLE")
                .name("디럭스 룸")
                .basePrice(new BigDecimal("150000.00"))
                .isActive(true)
                .hotel(testHotel)
                .roomTypeId("DELUXE")
                .roomKindId("NORMAL")
                .build();
        roomRepository.save(deluxeRoom);

        suiteRoom = Room.builder()
                .id(99993L)
                .roomNumber("T103")
                .status("AVAILABLE")
                .name("스위트 룸")
                .basePrice(new BigDecimal("300000.00"))
                .isActive(true)
                .hotel(testHotel)
                .roomTypeId("SUITE")
                .roomKindId("NORMAL")
                .build();
        roomRepository.save(suiteRoom);

        // 3. 회원 등록
        normalUser1 = User.builder()
                .id(999910L)
                .email("test_user1_unique@test.com")
                .password("password")
                .name("김철수")
                .role("CUSTOMER")
                .status("ACTIVE")
                .grade("SILVER")
                .build();
        userRepository.save(normalUser1);

        normalUser2 = User.builder()
                .id(999920L)
                .email("test_user2_unique@test.com")
                .password("password")
                .name("이영희")
                .role("CUSTOMER")
                .status("ACTIVE")
                .grade("GOLD")
                .build();
        userRepository.save(normalUser2);

        vipUser = User.builder()
                .id(999930L)
                .email("test_vip_unique@test.com")
                .password("password")
                .name("박대표")
                .role("CUSTOMER")
                .status("ACTIVE")
                .grade("VIP")
                .build();
        userRepository.save(vipUser);

        // 4. 예약 및 결제 설정 (2026-06 기준 테스트용)
        
        // 4.1. 5월 예약 1 (김철수 투숙 -> 6월 재방문 여부 체크용)
        createBookingAndPayment(
                999901L, normalUser1, testHotel, standardRoom, "BT260501", 1, 
                LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 11),
                new BigDecimal("100000.00"), LocalDateTime.of(2026, 5, 10, 14, 0)
        );

        // 4.2. 5월 예약 2 (박대표 VIP 투숙 -> 6월 VIP 재방문 여부 체크용)
        createBookingAndPayment(
                999902L, vipUser, testHotel, suiteRoom, "BT260502", 2, 
                LocalDate.of(2026, 5, 20), LocalDate.of(2026, 5, 22),
                new BigDecimal("600000.00"), LocalDateTime.of(2026, 5, 20, 12, 0)
        );

        // 4.3. 6월 예약 1 (김철수: 일반 재방문 유저 - 디럭스 150,000원 결제)
        createBookingAndPayment(
                999903L, normalUser1, testHotel, deluxeRoom, "BT260601", 1, 
                LocalDate.of(2026, 6, 5), LocalDate.of(2026, 6, 6),
                new BigDecimal("150000.00"), LocalDateTime.of(2026, 6, 5, 15, 0)
        );

        // 4.4. 6월 예약 2 (박대표: VIP 재방문 유저 - 스위트 900,000원 결제 - **오늘 29일 투숙 포함**)
        createBookingAndPayment(
                999904L, vipUser, testHotel, suiteRoom, "BT260602", 3, 
                LocalDate.of(2026, 6, 28), LocalDate.of(2026, 7, 1),
                new BigDecimal("900000.00"), LocalDateTime.of(2026, 6, 28, 10, 0)
        );

        // 4.5. 6월 예약 3 (이영희: 일반 신규 예약 - 스탠다드 100,000원 결제 - **오늘 29일 투숙 포함**)
        createBookingAndPayment(
                999905L, normalUser2, testHotel, standardRoom, "BT260603", 1, 
                LocalDate.of(2026, 6, 29), LocalDate.of(2026, 6, 30),
                new BigDecimal("100000.00"), LocalDateTime.of(2026, 6, 29, 11, 30)
        );

        // JPA 컨텍스트 데이터를 DB에 즉시 플러시하여 MyBatis 쿼리가 조회할 수 있도록 조치
        em.flush();
        em.clear();
    }

    private void createBookingAndPayment(
            Long id, User user, Hotel hotel, Room room, String bookingNo, int nights,
            LocalDate checkIn, LocalDate checkOut, BigDecimal amount, LocalDateTime paidAt
    ) {
        Booking booking = Booking.builder()
                .id(id)
                .user(user)
                .hotel(hotel)
                .room(room)
                .channelId(1L)
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

        Payment payment = Payment.builder()
                .id(id)
                .booking(booking)
                .paymentMethod("CARD")
                .paymentStatus(PaymentStatus.PAID)
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
        Long hotelId = testHotel.getId();
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
    }
}
