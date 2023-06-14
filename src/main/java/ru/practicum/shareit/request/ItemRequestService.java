package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto createRequest(int userId, ItemRequestDto itemRequest);

    Collection<ItemRequestDto> getRequests(int userId);

    Collection<ItemRequestDto> getRequestsAll(int userId, Integer from, Integer size);

    ItemRequestDto getRequests(int userId, int requestId);
}
