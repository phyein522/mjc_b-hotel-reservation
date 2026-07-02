package com.mjc.hotel.rooms.dto;

import com.mjc.hotel.common.util.BaseEntity;
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
@Entity(name = "rooms")
public class RoomEntity extends BaseEntity implements IRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "room_id")
	private Long roomId;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false, length = 50)
	private String number;

	@Column(nullable = false)
	private Integer floor;

	@Column(nullable = false)
	private Integer size;

	@Column(name = "base_price", nullable = false)
	private BigDecimal basePrice;

	@Column(name = "max_adult", nullable = false)
	private Integer maxAdult;

	@Column(name = "max_child", nullable = false)
	private Integer maxChild;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	@Column(name = "room_type", nullable = false)
	private RoomType roomType;

	@Column(name = "room_status", nullable = false)
	private RoomStatus roomStatus;

	@Column(name = "room_view_option", nullable = false)
	private RoomViewOption roomViewOption;

	@Column(name = "room_bed_option", nullable = false)
	private RoomBedOption roomBedOption;

	private Long hotelId;
}
