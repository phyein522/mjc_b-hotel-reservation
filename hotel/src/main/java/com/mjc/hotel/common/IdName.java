package com.mjc.hotel.common;

public interface IdName {
	Long getId();
	void setId(Long id);

	String getName();
	void setName(String name);

	default IdName copyMembers(IdName src, boolean forced) {
		if ( src == null ) {
			return this;
		}
		if ( forced || src.getId() != null ) {
			this.setId(src.getId());
		}
		if ( forced || src.getName() != null ) {
			this.setName(src.getName());
		}
		return this;
	}
}
