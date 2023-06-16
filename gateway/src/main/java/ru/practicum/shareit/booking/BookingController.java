package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

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
											@RequestParam(required = false) Integer from, Integer size) {
		return bookingClient.getBookingByUser(userId, state, from, size);
	}

	@GetMapping("/owner")
	@ResponseBody
	ResponseEntity<Object> getBookingByOwner(@RequestHeader(value = "X-Sharer-User-Id", required = false) long userId,
											 @RequestParam(defaultValue = "ALL", required = false) String state,
											 @RequestParam(required = false) Integer from, Integer size) {

		return bookingClient.getBookingByOwner(userId, state, from, size);
	}
}
