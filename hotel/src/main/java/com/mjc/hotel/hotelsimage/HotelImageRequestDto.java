package com.mjc.hotel.hotelsimage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelImageRequestDto extends HotelImageDto{
    private MultipartFile image;
}
