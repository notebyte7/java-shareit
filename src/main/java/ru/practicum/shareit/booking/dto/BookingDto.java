package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final int item;
    private final int booker;
    private final Status status;
}
