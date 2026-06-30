package com.mjc.hotel.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
public abstract class BaseDto implements IBase {

	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
}
