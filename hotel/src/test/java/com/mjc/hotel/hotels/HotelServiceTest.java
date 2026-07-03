package com.mjc.hotel.hotels;

import com.mjc.hotel.common.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
public class HotelServiceTest {
	@Autowired
	private HotelService hotelService;
	private static HotelDto HotelDto;

	@Test
	@Order(1)
	void createHotel() {
		HotelDto hotelDto = HotelDto.builder()
//				.name(Utils.getRamdomString(40))
				.city("서울").description("서울의 한 호텔")
				.address("중랑구 123").zipCode("12345").phone("01111111111")
				.email("email@eamil.com")
				.checkIn(LocalTime.of(15, 00)).checkOut(LocalTime.of(11, 00))
				.starRate((short) 3).isActive(true).latitude("33.3333").longitude("127.1111")
				.type(HotelTypeEnum.HOTEL).build();

		log.info("Hotel insert parameter : object = {}", hotelDto);
		assertThrows(DataIntegrityViolationException.class, () -> {
			hotelDto.setName(null);
			this.hotelService.insert(hotelDto);
		});

		hotelDto.setName(Utils.getRamdomString(20));
		log.info("Hotel insert parameter : object = {}", hotelDto);
		HotelDto inserted = this.hotelService.insert(hotelDto);
		log.info("Hotel insert result : object = {}", inserted);

		assertThat(inserted).isNotNull();
		assertThat(inserted.getHotelId()).isNotNull();
		assertThat(inserted.getHotelId()).isGreaterThanOrEqualTo(1L);
		assertThat(inserted.getName()).isEqualTo(hotelDto.getName());
		assertThat(inserted.getCity()).isEqualTo(hotelDto.getCity());
		assertThat(inserted.getAddress()).isEqualTo(hotelDto.getAddress());
		assertThat(inserted.getZipCode()).isEqualTo(hotelDto.getZipCode());
		assertThat(inserted.getPhone()).isEqualTo(hotelDto.getPhone());
		assertThat(inserted.getEmail()).isEqualTo(hotelDto.getEmail());
		assertThat(inserted.getCheckIn()).isEqualTo(hotelDto.getCheckIn());
		assertThat(inserted.getStarRate()).isEqualTo(hotelDto.getStarRate());
		assertThat(inserted.getType()).isEqualTo(hotelDto.getType());
		assertThat(inserted.getLatitude()).isEqualTo(hotelDto.getLatitude());
		assertThat(inserted.getLongitude()).isEqualTo(hotelDto.getLongitude());
		assertThat(inserted.getIsActive()).isEqualTo(hotelDto.getIsActive());

		HotelDto = (HotelDto) new HotelDto().copyMembers(inserted, true);
	}

	@Test
	@Order(2)
	void findHotel() {
		assertThat(HotelDto).isNotNull();
		assertThat(HotelDto.getHotelId()).isNotNull();
		assertThat(HotelDto.getHotelId()).isGreaterThanOrEqualTo(1L);

		log.info("Hotel find parameter : object = {}", HotelDto);
		HotelDto findHotel = this.hotelService.findById(HotelDto.getHotelId());
		log.info("Hotel find result : object = {}", findHotel);

		this.compareHotel(findHotel, HotelDto);
	}

	private void compareHotel(IHotel hotelA, IHotel hotelB) {
		assertThat(hotelA).isNotNull();
		assertThat(hotelB).isNotNull();
		assertThat(hotelA.getHotelId()).isEqualTo(hotelB.getHotelId());
		assertThat(hotelA.getName()).isEqualTo(hotelB.getName());
		assertThat(hotelA.getCity()).isEqualTo(hotelB.getCity());
		assertThat(hotelA.getAddress()).isEqualTo(hotelB.getAddress());
		assertThat(hotelA.getZipCode()).isEqualTo(hotelB.getZipCode());
		assertThat(hotelA.getPhone()).isEqualTo(hotelB.getPhone());
		assertThat(hotelA.getEmail()).isEqualTo(hotelB.getEmail());
		assertThat(hotelA.getCheckIn()).isEqualTo(hotelB.getCheckIn());
		assertThat(hotelA.getStarRate()).isEqualTo(hotelB.getStarRate());
		assertThat(hotelA.getType()).isEqualTo(hotelB.getType());
		assertThat(hotelA.getLatitude()).isEqualTo(hotelB.getLatitude());
		assertThat(hotelA.getLongitude()).isEqualTo(hotelB.getLongitude());
		assertThat(hotelA.getIsActive()).isEqualTo(hotelB.getIsActive());
	}
}
