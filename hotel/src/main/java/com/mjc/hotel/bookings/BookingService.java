package com.mjc.hotel.bookings;

import com.mjc.hotel.hotels.HotelDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
public class BookingService {
    @Autowired private BookingRepository bookingRepository;
    @Autowired private BookingMapper bookingMapper;

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

        HotelDto findHotel = this.getHotelByRoomId(insertEntity.getRoomId());
        insertEntity.setHotelId(findHotel.getHotelId());
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

    public HotelDto getHotelByRoomId(Long roomId) {
        HotelDto result = this.bookingMapper.getHotelByRoomId(roomId);
        return result;
    }

    public Integer calculateNights(LocalDate checkinDate, LocalDate checkoutDate) {
        return Math.toIntExact(ChronoUnit.DAYS.between(checkinDate, checkoutDate));
    }

    public List<BookingDto> findAllByUserId(Long userId) {
        List<BookingDto> result = this.bookingMapper.getBookings(userId);
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
