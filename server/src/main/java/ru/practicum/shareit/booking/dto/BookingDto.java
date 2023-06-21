package ru.practicum.shareit.booking.dto;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Value
public class BookingDto {
    Integer id;
    LocalDateTime start;
    LocalDateTime end;
    Integer itemId;
    Integer bookerId;
    StatusDto status;
}
