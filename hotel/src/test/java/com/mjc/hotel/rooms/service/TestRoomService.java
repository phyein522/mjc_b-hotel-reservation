package com.mjc.hotel.rooms.service;

import com.mjc.hotel.common.Utils;
import com.mjc.hotel.hotels.HotelDto;
import com.mjc.hotel.hotels.HotelService;
import com.mjc.hotel.hotels.HotelTypeEnum;
import com.mjc.hotel.rooms.dto.IRoom;
import com.mjc.hotel.rooms.dto.RoomDto;
import com.mjc.hotel.rooms.enums.RoomBedOption;
import com.mjc.hotel.rooms.enums.RoomStatus;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rooms.enums.RoomViewOption;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
public class TestRoomService {
	@Autowired
	private RoomService roomService;
	@Autowired
	private HotelService hotelService;

	private static RoomDto ROOM_DTO;

	// insert 테스트
	@Test
	@Order(1)
	public void testInsert() {
		// GIVEN 값을 초기화
		ROOM_DTO = RoomDto.builder()
				.roomId(null)
				.name(Utils.getRamdomString(50))
				.number("101")
				.floor(1)
				.size(20)
				.basePrice(new BigDecimal("100000.00"))
				.maxAdult(4)
				.maxChild(4)
				.isActive(true)
				.roomType(RoomType.Deluxe)
				.roomStatus(RoomStatus.EnableReservation)
				.roomViewOption(RoomViewOption.CityView)
				.roomBedOption(RoomBedOption.Floor)
				.hotelId(null)
				.build();

		// WHEN 실행 조건
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			log.error("fail insert : object = {}", ROOM_DTO);
			this.roomService.insert(ROOM_DTO);
		});

		HotelDto hotelDto = HotelDto.builder().name(Utils.getRamdomString(40))
				.city("서울").description("서울의 한 호텔")
				.address("중랑구 123").zipCode("12345").phone("01111111111")
				.email("email@eamil.com")
				.checkIn(LocalTime.of(15, 00)).checkOut(LocalTime.of(11, 00))
				.starRate((short) 3).isActive(true).latitude("33.3333").longitude("127.1111")
				.type(HotelTypeEnum.HOTEL).build();

		log.info("Hotel insert parameter : object = {}", hotelDto);
		HotelDto insertedHotel = this.hotelService.insert(hotelDto);
		log.info("Hotel insert result : object = {}", insertedHotel);
		ROOM_DTO.setHotelId(insertedHotel.getHotelId());
		log.info("Room insert parameter : object = {}", ROOM_DTO);
		RoomDto insertedRoom = this.roomService.insert(ROOM_DTO);
		log.info("Room insert result : object = {}", insertedRoom);
		assertThat(insertedRoom).isNotNull();
		assertThat(insertedRoom.getRoomId()).isGreaterThanOrEqualTo(1L);

		compareTwoRoom(insertedRoom, ROOM_DTO);

		// THEN 결과를 체크
		ROOM_DTO.setRoomId(insertedRoom.getRoomId());
	}

	// findyId 테스트
	@Test
	@Order(2)
	public void testFindById() {
		// GIVEN 값을 초기화
		
		// WHEN 실행 조건
		log.info("Room find parameter : object = {}", ROOM_DTO);
		RoomDto findDto = this.roomService.findById(ROOM_DTO.getRoomId());
		log.info("Room find result : object = {}", findDto);
		// THEN 결과를 체크

		assertThat(findDto.getRoomId()).isEqualTo(ROOM_DTO.getRoomId());
		compareTwoRoom(findDto, ROOM_DTO);
	}

	private void compareTwoRoom(IRoom roomA, IRoom roomB) {
		assertThat(roomA).isNotNull();
		assertThat(roomB).isNotNull();
		assertThat(roomA.getName()).isEqualTo(roomB.getName());
		assertThat(roomA.getNumber()).isEqualTo(roomB.getNumber());
		assertThat(roomA.getFloor()).isEqualTo(roomB.getFloor());
		assertThat(roomA.getSize()).isEqualTo(roomB.getSize());
		assertThat(roomA.getBasePrice()).isEqualTo(roomB.getBasePrice());
		assertThat(roomA.getMaxAdult()).isEqualTo(roomB.getMaxAdult());
		assertThat(roomA.getMaxChild()).isEqualTo(roomB.getMaxChild());
		assertThat(roomA.getIsActive()).isEqualTo(roomB.getIsActive());
		assertThat(roomA.getRoomType()).isEqualTo(roomB.getRoomType());
		assertThat(roomA.getRoomStatus()).isEqualTo(roomB.getRoomStatus());
		assertThat(roomA.getRoomViewOption()).isEqualTo(roomB.getRoomViewOption());
		assertThat(roomA.getRoomBedOption()).isEqualTo(roomB.getRoomBedOption());
		assertThat(roomA.getHotelId()).isEqualTo(roomB.getHotelId());
		assertThat(roomA.getHotel()).isNotNull();
		assertThat(roomB.getHotel()).isNotNull();
		assertThat(roomA.getHotel().getHotelId()).isEqualTo(roomB.getHotel().getHotelId());
	}

	// update 테스트
	@Test
	@Order(3)
	public void testUpdate() {
		// GIVEN 값을 초기화
		RoomDto updateDto = (RoomDto)new RoomDto().copyMembers(ROOM_DTO, true);
		updateDto.setName(Utils.getRamdomString(30));
		updateDto.setBasePrice(new BigDecimal("110000.00"));
		updateDto.setMaxAdult(5);

		// WHEN 실행 조건
		log.info("Room update parameter : object = {}", updateDto);
		this.roomService.update(updateDto);
		RoomDto findDto = this.roomService.findById(updateDto.getRoomId());
		log.info("Room update find result : object = {}", findDto);

		// THEN 결과를 체크
		assertThat(findDto.getRoomId()).isEqualTo(updateDto.getRoomId());
		compareTwoRoom(findDto, updateDto);
	}

	// delete 테스트
	@Test
	@Order(4)
	public void testDelete() {
		// GIVEN 값을 초기화

		// WHEN 실행 조건
		assertThrows(NoSuchElementException.class, () -> {
			this.roomService.deleteById(0L);
		});

		log.info("Room delete parameter : object = {}", ROOM_DTO);
		this.roomService.deleteById(ROOM_DTO.getRoomId());

		// THEN 결과를 체크
		assertThrows(NoSuchElementException.class, () -> {
			this.roomService.findById(ROOM_DTO.getRoomId());
		});
	}
}
