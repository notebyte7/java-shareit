package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exeption.ForbiddenException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.WrongCommandException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingMapper.toBookingShortForItem;
import static ru.practicum.shareit.item.CommentMapper.toComment;
import static ru.practicum.shareit.item.CommentMapper.toCommentWithName;
import static ru.practicum.shareit.item.ItemMapper.*;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository, ItemRequestRepository requestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User не существует"));
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            int requestId = itemDto.getRequestId();
            itemRequest = requestRepository.findById(requestId).get();
        }
        Item item = toItem(itemDto, owner, itemRequest);
        return toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto) {
        if (itemRepository.findById(itemId).isPresent()) {
            User owner = userRepository.findById(userId).get();
            Item item = toItem(itemDto, owner);
            Item updatedItem = itemRepository.findById(itemId).get();
            if (updatedItem.getOwner().getId() == userId) {
                if (item.getName() != null) {
                    updatedItem.setName(item.getName());
                }
                if (item.getDescription() != null) {
                    updatedItem.setDescription(item.getDescription());
                }
                if (item.getAvailable() != null) {
                    updatedItem.setAvailable(item.getAvailable());
                }
                return toItemDto(itemRepository.save(updatedItem));
            } else {
                throw new ForbiddenException("Доступ закрыт, нельзя менять item не его владельцу");
            }
        } else {
            throw new NotFoundException("Такого Item не существует");
        }
    }

    @Override
    public ItemOutputDto getItemDtoById(int userId, int itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item не существует"));
        if (item.getOwner().getId() == userId) {
            Collection<Booking> bookings = bookingRepository.findBookingsByItemId(itemId);
            BookingShortDto lastBooking = null;
            BookingShortDto nextBooking = null;
            if (bookings.size() != 0) {
                lastBooking = getLastBooking(bookings, itemId);
                nextBooking = getNextBooking(bookings, itemId);
            }
            return toItemWithBooking(item, lastBooking, nextBooking);
        } else {
            return toItemWithBooking(item, null, null);
        }
    }

    @Override
    public Collection<ItemOutputDto> getItemsByOwner(int ownerId, Integer from, Integer size) {
        Collection<Item> items;
        if (from != null && size != null) {
            if (from >= 0 && size > 0) {
                Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").descending());
                items = itemRepository.searchByOwner(pageable, ownerId);
            } else {
                throw new WrongCommandException("Неправильный запрос from и size");
            }

        } else {
            items = itemRepository.searchByOwner(ownerId);
        }
        Collection<Booking> bookings = bookingRepository.findByOwner(ownerId);
        Collection<ItemOutputDto> itemsWithBooking = new ArrayList<>();
        for (Item item : items) {
            BookingShortDto lastBooking = null;
            BookingShortDto nextBooking = null;
            if (bookings.size() != 0) {
                lastBooking = getLastBooking(bookings, item.getId());
                nextBooking = getNextBooking(bookings, item.getId());
            }
            ItemOutputDto itemWithBookings = toItemWithBooking(item, lastBooking, nextBooking);
            itemsWithBooking.add(itemWithBookings);
        }
        return itemsWithBooking;
    }

    @Override
    public Collection<ItemDto> searchItemsByText(String text, Integer from, Integer size) {
        if (from != null && size != null) {
            if (from >= 0 && size > 0) {
                if (text.length() > 0) {
                    text = text.toLowerCase();
                    Pageable pageable = PageRequest.of(from / size, size,
                            Sort.by("id").descending());
                    return itemRepository.search(pageable, text).stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                } else {
                    return new ArrayList<>();
                }
            } else {
                throw new WrongCommandException("Неправильный запрос from и size");
            }
        } else {
            if (text.length() > 0) {
                text = text.toLowerCase();
                return itemRepository.search(text).stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList());
            } else {
                return new ArrayList<>();
            }
        }
    }

    @Override
    public CommentOutputDto createComment(int userId, int itemId, CommentDto commentDto) {
        if (itemRepository.findById(itemId).isPresent()) {
            Collection<Booking> bookings = bookingRepository.findBookingsByItemId(itemId);
            if (bookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(itemId))
                    .filter(booking -> booking.getStatus().equals(Status.APPROVED))
                    .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .anyMatch(booking -> booking.getBooker().getId().equals(userId))) {
                User user = userRepository.findById(userId).get();
                Comment comment = toComment(commentDto, user);
                comment.setItemId(itemId);
                comment.setCreated(LocalDateTime.now());
                commentRepository.save(comment);
                return toCommentWithName(comment);
            } else {
                throw new WrongCommandException("Нельзя создать комментарий не пользовавшись вещью");
            }
        } else {
            throw new WrongCommandException("Вещи для создания комментария не существует");
        }
    }

    private BookingShortDto getLastBooking(Collection<Booking> bookings, int itemId) {
        Booking booking = bookings.stream()
                .filter(b -> b.getItem().getId() == itemId)
                .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                .filter(b -> b.getStatus().equals(Status.APPROVED) || b.getStatus().equals(Status.WAITING))
                .max(Comparator.comparing(Booking::getEnd)).orElse(null);
        if (booking != null) {
            return toBookingShortForItem(booking);
        } else {
            return null;
        }
    }

    private BookingShortDto getNextBooking(Collection<Booking> bookings, int itemId) {
        Booking booking = bookings.stream()
                .filter(b -> b.getItem().getId() == itemId)
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .filter(b -> b.getStatus().equals(Status.APPROVED) || b.getStatus().equals(Status.WAITING))
                .min(Comparator.comparing(Booking::getStart)).orElse(null);
        if (booking != null) {
            return toBookingShortForItem(booking);
        } else {
            return null;
        }
    }
}
