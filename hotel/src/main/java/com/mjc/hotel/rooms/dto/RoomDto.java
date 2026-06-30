package com.mjc.hotel.rooms.dto;

import com.mjc.hotel.common.dto.BaseDto;
import com.mjc.hotel.rooms.enums.RoomBedOption;
import com.mjc.hotel.rooms.enums.RoomStatus;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rooms.enums.RoomViewOption;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RoomDto implements IRoom {
	private Long roomId;
	private String name;
	private String number;
	private Integer floor;
	private Integer size;
	private BigDecimal basePrice;
	private Integer maxAdult;
	private Integer maxChild;
	private Boolean isActive;
	private RoomType roomType;
	private RoomStatus roomStatus;
	private RoomViewOption roomViewOption;
	private RoomBedOption roomBedOption;
	private Long hotelId;
}
