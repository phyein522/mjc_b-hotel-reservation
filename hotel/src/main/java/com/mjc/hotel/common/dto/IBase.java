package com.mjc.hotel.common.dto;

import java.time.LocalDateTime;

public interface IBase {
	LocalDateTime getCreatedAt();
	void setCreatedAt(LocalDateTime createdAt);

	LocalDateTime getModifiedAt();
	void setModifiedAt(LocalDateTime modifiedAt);

	default IBase copyMembers(IBase source, boolean forced) {
		if ( source == null ) {
			return this;
		}
		if ( forced || source.getCreatedAt() != null ) {
			this.setCreatedAt(source.getCreatedAt());
		}
		if ( forced || source.getModifiedAt() != null ) {
			this.setModifiedAt(source.getModifiedAt());
		}
		return this;
	}
}
