package com.mjc.hotel.sales_analysis.util;

import com.mjc.hotel.sales_analysis.entity.Booking;
import com.mjc.hotel.sales_analysis.entity.Hotel;
import com.mjc.hotel.sales_analysis.entity.Payment;
import com.mjc.hotel.sales_analysis.entity.PaymentStatus;
import com.mjc.hotel.sales_analysis.entity.Room;
import com.mjc.hotel.sales_analysis.repository.BookingRepository;
import com.mjc.hotel.sales_analysis.repository.HotelRepository;
import com.mjc.hotel.sales_analysis.repository.PaymentRepository;
import com.mjc.hotel.sales_analysis.repository.RoomRepository;
import com.mjc.hotel.user.entity.User;
import com.mjc.hotel.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SalesAnalysisDummyDataRunner implements CommandLineRunner {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 중복 적재 방지 (호텔 ID 1번이 이미 존재하면 스킵)
        if (hotelRepository.existsById(1L)) {
            log.info("▶ [MJC 대시보드] ID 1번 호텔이 이미 존재하여 더미 데이터 자동 적재를 스킵합니다.");
            return;
        }

        log.info("▶ [MJC 대시보드] 대시보드 검증용 더미 데이터 자동 적재를 시작합니다...");

        try {
            // 1. 호텔 등록 (ID: 1L)
            Hotel hotel = Hotel.builder()
                    .id(1L)
                    .name("그랜드 MJC 호텔")
                    .address("서울시 강남구 테헤란로 123")
                    .city("서울")
                    .zipCode("06123")
                    .phoneNumber("02-1234-5678")
                    .email("info@mjchotel.com")
                    .checkInTime(LocalTime.of(15, 0))
                    .checkOutTime(LocalTime.of(11, 0))
                    .starRating(5)
                    .isActive(true)
                    .hotelType("CITY")
                    .build();
            hotelRepository.save(hotel);

            // 2. 객실 등록 (ID: 101L, 102L, 103L)
            Room standardRoom = Room.builder()
                    .id(101L)
                    .roomNumber("101")
                    .status("AVAILABLE")
                    .name("스탠다드 룸")
                    .basePrice(new BigDecimal("100000.00"))
                    .isActive(true)
                    .hotel(hotel)
                    .roomTypeId("STANDARD")
                    .roomKindId("NORMAL")
                    .build();
            roomRepository.save(standardRoom);

            Room deluxeRoom = Room.builder()
                    .id(102L)
                    .roomNumber("102")
                    .status("AVAILABLE")
                    .name("디럭스 룸")
                    .basePrice(new BigDecimal("150000.00"))
                    .isActive(true)
                    .hotel(hotel)
                    .roomTypeId("DELUXE")
                    .roomKindId("NORMAL")
                    .build();
            roomRepository.save(deluxeRoom);

            Room suiteRoom = Room.builder()
                    .id(103L)
                    .roomNumber("103")
                    .status("AVAILABLE")
                    .name("스위트 룸")
                    .basePrice(new BigDecimal("300000.00"))
                    .isActive(true)
                    .hotel(hotel)
                    .roomTypeId("SUITE")
                    .roomKindId("NORMAL")
                    .build();
            roomRepository.save(suiteRoom);

            // 3. 회원 등록 (ID: 10L, 20L, 30L)
            User normalUser1 = User.builder()
                    .id(10L)
                    .email("customer1@test.com")
                    .password("password123")
                    .name("김철수")
                    .role("CUSTOMER")
                    .status("ACTIVE")
                    .grade("SILVER")
                    .build();
            userRepository.save(normalUser1);

            User normalUser2 = User.builder()
                    .id(20L)
                    .email("customer2@test.com")
                    .password("password123")
                    .name("이영희")
                    .role("CUSTOMER")
                    .status("ACTIVE")
                    .grade("GOLD")
                    .build();
            userRepository.save(normalUser2);

            User vipUser = User.builder()
                    .id(30L)
                    .email("vip1@test.com")
                    .password("password123")
                    .name("박대표")
                    .role("CUSTOMER")
                    .status("ACTIVE")
                    .grade("VIP")
                    .build();
            userRepository.save(vipUser);

            // 4. 예약 및 결제 등록 (최근 7개월 트렌드 구성용)

            // 2025년 12월
            createBookingAndPayment(1L, normalUser1, hotel, standardRoom, "B251201", 1, 
                    LocalDate.of(2025, 12, 15), LocalDate.of(2025, 12, 16),
                    new BigDecimal("100000.00"), LocalDateTime.of(2025, 12, 15, 14, 0));

            // 2026년 1월
            createBookingAndPayment(2L, normalUser2, hotel, deluxeRoom, "B260101", 2, 
                    LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 12),
                    new BigDecimal("300000.00"), LocalDateTime.of(2026, 1, 10, 14, 30));

            // 2026년 2월
            createBookingAndPayment(3L, vipUser, hotel, deluxeRoom, "B260201", 1, 
                    LocalDate.of(2026, 2, 14), LocalDate.of(2026, 2, 15),
                    new BigDecimal("150000.00"), LocalDateTime.of(2026, 2, 14, 11, 0));

            // 2026년 3월
            createBookingAndPayment(4L, normalUser1, hotel, suiteRoom, "B260301", 1, 
                    LocalDate.of(2026, 3, 20), LocalDate.of(2026, 3, 21),
                    new BigDecimal("300000.00"), LocalDateTime.of(2026, 3, 20, 15, 0));

            // 2026년 4월
            createBookingAndPayment(5L, normalUser2, hotel, standardRoom, "B260401", 2, 
                    LocalDate.of(2026, 4, 5), LocalDate.of(2026, 4, 7),
                    new BigDecimal("200000.00"), LocalDateTime.of(2026, 4, 5, 13, 0));

            // 2026년 5월 (전월 지표 비교)
            createBookingAndPayment(6L, normalUser1, hotel, standardRoom, "B260501", 1, 
                    LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 11),
                    new BigDecimal("100000.00"), LocalDateTime.of(2026, 5, 10, 14, 0));

            createBookingAndPayment(7L, vipUser, hotel, suiteRoom, "B260502", 2, 
                    LocalDate.of(2026, 5, 20), LocalDate.of(2026, 5, 22),
                    new BigDecimal("600000.00"), LocalDateTime.of(2026, 5, 20, 12, 0));

            // 2026년 6월 (이번달 지표)
            createBookingAndPayment(8L, normalUser1, hotel, deluxeRoom, "B260601", 1, 
                    LocalDate.of(2026, 6, 5), LocalDate.of(2026, 6, 6),
                    new BigDecimal("150000.00"), LocalDateTime.of(2026, 6, 5, 15, 0));

            createBookingAndPayment(9L, vipUser, hotel, suiteRoom, "B260602", 3, 
                    LocalDate.of(2026, 6, 28), LocalDate.of(2026, 7, 1),
                    new BigDecimal("900000.00"), LocalDateTime.of(2026, 6, 28, 10, 0));

            createBookingAndPayment(10L, normalUser2, hotel, standardRoom, "B260603", 1, 
                    LocalDate.of(2026, 6, 29), LocalDate.of(2026, 6, 30),
                    new BigDecimal("100000.00"), LocalDateTime.of(2026, 6, 29, 11, 30));

            log.info("▶ [MJC 대시보드] 더미 데이터 자동 적재가 성공적으로 완료되었습니다!");

        } catch (Exception e) {
            log.error("▶ [MJC 대시보드] 더미 데이터 적재 중 에러가 발생했습니다: ", e);
        }
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
}
