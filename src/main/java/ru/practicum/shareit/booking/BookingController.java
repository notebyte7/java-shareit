package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.exeption.ErrorResponse;
import ru.practicum.shareit.exeption.WrongStateException;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseBody
    BookingOutputDto createBooking(@RequestHeader("X-Sharer-User-Id") int bookerId, @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseBody
    BookingOutputDto approvingBooking(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int bookingId,
                                      @RequestParam boolean approved) {
        return bookingService.approvingBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseBody
    BookingOutputDto getBookingById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping()
    @ResponseBody
    List<BookingOutputDto> getBookingByUser(@RequestHeader(value = "X-Sharer-User-Id", required = false) int userId,
                                            @RequestParam(defaultValue = "ALL", required = false) String state,
                                            @RequestParam(required = false) Integer from, Integer size) {
        State stateEnum;
        try {
            stateEnum = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new WrongStateException(String.format("Unknown state: %s", state));
        }
        return bookingService.getBookingByUser(userId, stateEnum, from, size);
    }

    @GetMapping("/owner")
    @ResponseBody
    List<BookingOutputDto> getBookingByOwner(@RequestHeader(value = "X-Sharer-User-Id", required = false) int userId,
                                             @RequestParam(defaultValue = "ALL", required = false) String state,
                                             @RequestParam(required = false) Integer from, Integer size) {
        State stateStatus;
        try {
            stateStatus = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new WrongStateException(String.format("Unknown state: %s", state));
        }
        return bookingService.getBookingByOwner(userId, stateStatus, from, size);
    }

    @ExceptionHandler(WrongStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }
}
