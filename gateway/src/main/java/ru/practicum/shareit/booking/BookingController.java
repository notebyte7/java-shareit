package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

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
}
