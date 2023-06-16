package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService requestService;

    public ItemRequestController(ItemRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    @ResponseBody
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                        @RequestBody @Valid ItemRequestDto itemRequest) {
        return requestService.createRequest(userId, itemRequest);
    }

    @GetMapping
    @ResponseBody
    public Collection<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") int userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping("/{requestId}")
    @ResponseBody
    public ItemRequestDto getRequests(@RequestHeader("X-Sharer-User-Id") int userId,
                                      @PathVariable int requestId) {
        return requestService.getRequests(userId, requestId);
    }

    @GetMapping("/all")
    @ResponseBody
    public Collection<ItemRequestDto> getRequestsAll(@RequestHeader("X-Sharer-User-Id") int userId,
                                                     @RequestParam(required = false) Integer from, Integer size) {
        return requestService.getRequestsAll(userId, from, size);
    }
}
