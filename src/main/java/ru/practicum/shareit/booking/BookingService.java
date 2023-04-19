package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;

import java.util.List;

public interface BookingService {
    BookingOutputDto createBooking(int bookerId, BookingDto bookingDto);

    BookingOutputDto approvingBooking(int userId, int bookingId, boolean approved);

    BookingOutputDto getBookingById(int userId, int bookingId);

    List<BookingOutputDto> getBookingByUser(int userId, State state);

    List<BookingOutputDto> getBookingByOwner(int userId, State state);
}
