package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoWithItemAndBooker;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingShortForItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public static BookingDtoWithItemAndBooker toBookingDtoWithItemAndBooker(Booking booking) {
        return new BookingDtoWithItemAndBooker(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDto bookingDto, Item item, User user) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                bookingDto.getStatus()
        );
    }

    public static BookingShortForItem toBookingShortForItem(Booking booking) {
        return new BookingShortForItem(
                booking.getId(),
                booking.getBooker().getId()
        );
    }
}
