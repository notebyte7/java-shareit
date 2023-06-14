package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.WrongCommandException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequest;
import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequestDto;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    public ItemRequestServiceImpl(ItemRequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemRequestDto createRequest(int userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = toItemRequest(itemRequestDto);
        User requestor = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        return toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public Collection<ItemRequestDto> getRequests(int userId) {
        if (userRepository.findById(userId).isPresent()) {
            return requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId).stream()
                    .map(ItemRequestMapper::toItemRequestDto)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Пользователь не существует");
        }
    }

    @Override
    public ItemRequestDto getRequests(int userId, int requestId) {
        if (userRepository.findById(userId).isPresent()) {
            ItemRequest request = requestRepository.findById(requestId)
                    .orElseThrow((NotFoundException::new));
            return toItemRequestDto(request);


        } else {
            throw new NotFoundException("Пользователь не существует");
        }
    }

    @Override
    public Collection<ItemRequestDto> getRequestsAll(int userId, Integer from, Integer size) {
        if (from != null && size != null) {
            if (from >= 0 && size > 0) {
                Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
                return requestRepository.findByRequestorIdNotOrderByCreatedDesc(userId, pageable).getContent().stream()
                        .map(ItemRequestMapper::toItemRequestDto)
                        .collect(Collectors.toList());
            } else {
                throw new WrongCommandException("Неправильный запрос from и size");
            }
        } else {
            return new ArrayList<>();
        }
    }
}
