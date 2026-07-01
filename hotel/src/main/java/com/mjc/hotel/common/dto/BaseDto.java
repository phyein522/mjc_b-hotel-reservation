package com.mjc.hotel.common.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
public abstract class BaseDto implements IBase {

	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
}
