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
    private final BookingServiceImpl bookingService;

    public BookingController(BookingServiceImpl bookingService) {
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
                                            @RequestParam(defaultValue = "ALL", required = false) String state) {
        try {
            return bookingService.getBookingByUser(userId, State.valueOf(state));
        } catch (IllegalArgumentException e) {
            throw new WrongStateException(String.format("Unknown state: %s", state));
        }
    }

    @GetMapping("/owner")
    @ResponseBody
    List<BookingOutputDto> getBookingByOwner(@RequestHeader(value = "X-Sharer-User-Id", required = false) int userId,
                                             @RequestParam(defaultValue = "ALL", required = false) String state) {
        try {
            return bookingService.getBookingByOwner(userId, State.valueOf(state));
        } catch (IllegalArgumentException e) {
            throw new WrongStateException(String.format("Unknown state: %s", state));
        }
    }

    @ExceptionHandler(WrongStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }
}
