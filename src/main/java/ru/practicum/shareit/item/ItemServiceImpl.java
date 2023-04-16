package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShortForItem;
import ru.practicum.shareit.exeption.ForbiddenException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithLastAndNextBookingAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingMapper.toBookingShortForItem;
import static ru.practicum.shareit.item.ItemMapper.*;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) {
        if (userRepository.findById(userId).isPresent()) {
            User owner = userRepository.findById(userId).get();
            Item item = toItem(itemDto, owner);
            return toItemDto(itemRepository.save(item));
        } else {
            throw new NotFoundException("User not found");
        }
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
    public ItemWithLastAndNextBookingAndComments getItemDtoById(int userId, int itemId) {
        if (itemRepository.findById(itemId).isPresent()) {
            Item item = itemRepository.findById(itemId).get();
            if (item.getOwner().getId() == userId) {
                Collection<Booking> bookings = bookingRepository.findBookingsByItem_Id(itemId);
                BookingShortForItem lastBooking = null;
                BookingShortForItem nextBooking = null;
                if (bookings.size() != 0) {
                    lastBooking = getLastBooking(bookings);
                    nextBooking = getNextBooking(bookings);
                }
                return toItemWithBooking(item, lastBooking, nextBooking);
            } else {
                return toItemWithBooking(item, null, null);
            }
        } else {
            throw new NotFoundException("Item not found");
        }
    }

    @Override
    public Collection<ItemWithLastAndNextBookingAndComments> getItemsByOwner(int ownerId) {
        Collection<Integer> itemIds = itemRepository.searchItem_IdByOwner(ownerId, LocalDateTime.now());
        Collection<ItemWithLastAndNextBookingAndComments> itemsWithBooking = new ArrayList<>();
        for (Integer id : itemIds) {
            Item item = itemRepository.findById(id).get();
            Collection<Booking> bookings = bookingRepository.findBookingsByItem_Id(item.getId());
            BookingShortForItem lastBooking = null;
            BookingShortForItem nextBooking = null;
            if (bookings.size() != 0) {
                lastBooking = getLastBooking(bookings);
                nextBooking = getNextBooking(bookings);
            }
            ItemWithLastAndNextBookingAndComments itemWithBookings = toItemWithBooking(item, lastBooking, nextBooking);
            itemsWithBooking.add(itemWithBookings);


        }
        return itemsWithBooking;
    }

    @Override
    public Collection<ItemDto> searchItemsByTest(String text) {
        if (text.length() > 0) {
            text = text.toLowerCase();
            return itemRepository.search(text).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    BookingShortForItem getLastBooking(Collection<Booking> bookings) {
        if (bookings != null) {
            return toBookingShortForItem(Objects.requireNonNull(bookings.stream()
                    .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(Booking::getEnd)).orElse(null)));
        } else {
            return null;
        }
    }

    BookingShortForItem getNextBooking(Collection<Booking> bookings) {
        if (bookings != null) {
            return toBookingShortForItem(Objects.requireNonNull(bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getEnd)).orElse(null)));
        } else {
            return null;
        }
    }
}
