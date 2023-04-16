package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoWithItemAndBooker;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingDtoWithItemAndBooker createBooking(int bookerId, BookingDto bookingDto);

    BookingDtoWithItemAndBooker approvingBooking(int userId, int bookingId, boolean approved);

    BookingDtoWithItemAndBooker getBookingById(int userId, int bookingId);

    List<BookingDtoWithItemAndBooker> getBookingByUser(int userId, State state);

    List<BookingDtoWithItemAndBooker> getBookingByOwner(int userId, State state);
}
