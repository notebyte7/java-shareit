package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exeption.ErrorResponse;
import ru.practicum.shareit.exeption.WrongStateException;

import java.util.Objects;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseBody
    ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") int bookerId, @RequestBody BookingDto bookingDto) {
        return bookingClient.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseBody
    ResponseEntity<Object> approvingBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId,
                                            @RequestParam boolean approved) {
        return bookingClient.approvingBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseBody
    ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping()
    @ResponseBody
    ResponseEntity<Object> getBookingByUser(@RequestHeader(value = "X-Sharer-User-Id", required = false) long userId,
                                            @RequestParam(defaultValue = "ALL", required = false) String state,
                                            @RequestParam(defaultValue = "0", required = false) Integer from,
                                            @RequestParam(defaultValue = "10", required = false) Integer size) {
        return bookingClient.getBookingByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseBody
    ResponseEntity<Object> getBookingByOwner(@RequestHeader(value = "X-Sharer-User-Id", required = false) long userId,
                                             @RequestParam(defaultValue = "ALL", required = false) String state,
                                             @RequestParam(defaultValue = "0", required = false) Integer from,
                                             @RequestParam(defaultValue = "10", required = false) Integer size) {

        return bookingClient.getBookingByOwner(userId, state, from, size);
    }


    @ExceptionHandler({BindException.class, HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final Exception e) {
        String message;
        String defaultMessage = "Invalid data format: ";
        if (e instanceof BindException) {
            message = defaultMessage + Objects.requireNonNull(((BindException) e).getFieldError()).getField();
        } else if (e.getClass().equals(HttpMessageNotReadableException.class)) {
            message = defaultMessage + ((HttpMessageNotReadableException) e).getMostSpecificCause();
        } else if (e.getClass().equals(MethodArgumentTypeMismatchException.class)) {
            message = defaultMessage + "request data";
        } else {
            message = e.getMessage();
        }
        return new ErrorResponse(message);
    }


    @ExceptionHandler({Throwable.class, WrongStateException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse(e.getMessage());
    }
}
