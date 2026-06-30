package com.mjc.hotel.rooms.dto;

import com.mjc.hotel.rooms.enums.RoomBedOption;
import com.mjc.hotel.rooms.enums.RoomStatus;
import com.mjc.hotel.rooms.enums.RoomType;
import com.mjc.hotel.rooms.enums.RoomViewOption;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity(name = "room")
public class RoomEntity implements IRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roomId;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false, length = 50)
	private String number;

	@Column(nullable = false)
	private Integer floor;

	@Column(nullable = false)
	private Integer size;

	@Column(nullable = false)
	private BigDecimal basePrice;

	@Column(nullable = false)
	private Integer maxAdult;

	@Column(nullable = false)
	private Integer maxChild;

	@Column(nullable = false)
	private Boolean isActive;

	@Column(nullable = false)
	private RoomType roomType;

	@Column(nullable = false)
	private RoomStatus roomStatus;

	@Column(nullable = false)
	private RoomViewOption roomViewOption;

	@Column(nullable = false)
	private RoomBedOption roomBedOption;

	@Column(nullable = false)
	private Long hotelId;
}
