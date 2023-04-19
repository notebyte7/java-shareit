package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.StatusDto;
import ru.practicum.shareit.booking.model.Status;

public class StatusMapper {
    public static StatusDto toStatusDto(Status status) {
        if (status != null) {
            return StatusDto.valueOf(status.toString());
        } else {
            return null;
        }
    }

    public static Status toStatus(StatusDto status) {
        if (status != null) {
            return Status.valueOf(status.toString());
        } else {
            return null;
        }
    }
}
