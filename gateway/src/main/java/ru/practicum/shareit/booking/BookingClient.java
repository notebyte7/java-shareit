package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exeption.WrongCommandException;
import ru.practicum.shareit.exeption.WrongStateException;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookingById(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingByUser(long userId, String state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", getState(state),
                "from", from,
                "size", size
        );
        if (from >= 0 && size > 0) {
            return get("?state={state}&from={from}&size={size}", userId, parameters);
        } else {
            throw new WrongCommandException("Неправильный запрос from и size");
        }
    }

    public ResponseEntity<Object> getBookingByOwner(long userId, String state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", getState(state),
                "from", from,
                "size", size
        );
        if (from >= 0 && size > 0) {
            return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
        } else {
            throw new WrongCommandException("Неправильный запрос from и size");
        }
    }


    public ResponseEntity<Object> createBooking(long userId, BookingDto bookingDto) {
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> approvingBooking(long userId, Long bookingId, Boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId, approved);
    }

    private State getState(String state) {
        State stateStatus;
        try {
            stateStatus = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new WrongStateException(String.format("Unknown state: %s", state));
        }
        return stateStatus;
    }
}
