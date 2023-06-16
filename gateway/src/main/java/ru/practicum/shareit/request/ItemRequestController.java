package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsAll(@RequestHeader(USER_ID_HEADER) Long userId,
                                               @RequestParam(defaultValue = "0", required = false) Integer from,
                                               @RequestParam(defaultValue = "10", required = false) Integer size) {
        return itemRequestClient.getRequestsAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @PathVariable Long requestId) {
        return itemRequestClient.getRequests(userId, requestId);
    }
}