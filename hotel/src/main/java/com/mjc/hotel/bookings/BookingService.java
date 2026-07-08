package com.mjc.hotel.bookings;

import com.mjc.hotel.hotels.HotelDto;
import com.mjc.hotel.hotelsimage.HotelImageResponseDto;
import com.mjc.hotel.room_images.dto.RoomImageResponseDto;
import com.mjc.hotel.rooms.dto.RoomDto;
import com.mjc.hotel.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BookingService {
    @Autowired private BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    public BookingDto insert(BookingDto insertDto) {
        BookingEntity insertEntity = (BookingEntity)new BookingEntity().copyMembers(insertDto, true);
        insertEntity.setBookingId(null);
        insertEntity.setBookingNo(generateBookingNo());
        insertEntity.setCancelledAt(null);
        insertEntity.setPoint(insertEntity.getTotalAmount() / 100);
        insertEntity.setNights(this.calculateNights(
                insertEntity.getCheckinDate()
                , insertEntity.getCheckoutDate()
        ));

        HotelDto findHotel = this.bookingMapper.getHotelByRoomId(insertEntity.getRoomId());
        insertEntity.setCheckinTime(findHotel.getCheckIn());
        insertEntity.setCheckoutTime(findHotel.getCheckOut());

        BookingEntity save = this.bookingRepository.save(insertEntity);
        BookingDto result = (BookingDto)new BookingDto().copyMembers(save, true);
        return result;
    }

    public String generateBookingNo() {
        LocalDate today = LocalDate.now();
        String year = String.valueOf(today.getYear());
        String month = String.format("%02d", today.getMonthValue());
        String day = String.format("%02d", today.getDayOfMonth());

        String sample = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        sb.append("SN")
                .append("-")
                .append(year)
                .append("-")
                .append(month)
                .append(day)
                .append("-");
        for(int i = 0; i < 4; i++) {
            sb.append(sample.charAt(random.nextInt(sample.length())));
        }
        return sb.toString();   //SN-yyyy-MMdd-0000
    }

    public Integer calculateNights(LocalDate checkinDate, LocalDate checkoutDate) {
        return Math.toIntExact(ChronoUnit.DAYS.between(checkinDate, checkoutDate));
    }

    public List<BookingResponseDto> findAllByUserId(Long userId) {
        UserDto user = this.bookingMapper.getUser(userId);

        List<BookingDto> bookings = this.bookingMapper.getBookingsByUserId(userId);
        List<BookingResponseDto> result = new ArrayList<>();
        for(BookingDto booking : bookings) {
            RoomDto room = this.bookingMapper.getRoom(booking.getRoomId());
            List<RoomImageResponseDto> roomImages = this.bookingMapper.getRoomImages(room.getRoomId());
            HotelDto hotel = this.bookingMapper.getHotelByRoomId(booking.getRoomId());
            List<HotelImageResponseDto> hotelImages = this.bookingMapper.getHotelImages(hotel.getHotelId());
            result.add(new BookingResponseDto(booking, user, room, roomImages, hotel, hotelImages));
        }
        //TODO: url도 같이 넘어오는지 테스트 시 확인
        return result;
    }

    public BookingDto cancel(Long bookingId) {
        BookingDto cancelDto = this.findById(bookingId);
        BookingEntity cancelEntity = (BookingEntity)new BookingEntity().copyMembers(cancelDto, true);
        cancelEntity.setCancelledAt(LocalDateTime.now());
        BookingEntity save = this.bookingRepository.save(cancelEntity);
        BookingDto result = (BookingDto)new BookingDto().copyMembers(save, true);
        return result;
    }

    public BookingDto findById(Long bookingId) {
        BookingEntity find = this.bookingRepository.findById(bookingId).orElseThrow();
        BookingDto result = (BookingDto)new BookingDto().copyMembers(find, true);
        return result;
    }
}
