package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.WrongCommandException;
import ru.practicum.shareit.exeption.WrongStateException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.BookingMapper.toBookingDtoWithItemAndBooker;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingOutputDto createBooking(int bookerId, BookingDto bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item не существует"));
        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User не существует"));
        Booking booking = toBooking(bookingDto, item,
                user);
        if (booking.getItem().getOwner().getId() != bookerId) {
            if (booking.getStart() != null && booking.getEnd() != null) {
                if (booking.getItem().getAvailable().equals(true)) {
                    if (booking.getStart().isAfter(LocalDateTime.now()) && booking.getStart().isBefore(booking.getEnd())
                            && !booking.getStart().isEqual(booking.getEnd())) {
                        if (booking.getStatus() == null) {
                            booking.setStatus(Status.WAITING);
                        }
                        return toBookingDtoWithItemAndBooker(bookingRepository.save(booking));
                    } else {
                        throw new WrongCommandException("Неправильно задано время начала и(или) конца бронированию");
                    }
                } else {
                    throw new WrongCommandException("Вещь недоступна");
                }
            } else {
                throw new WrongCommandException("Не задано время начала и(или) конца бронированию");
            }
        } else {
            throw new NotFoundException("Невозможно создать заявку на свою вещь");
        }
    }

    @Override
    public BookingOutputDto approvingBooking(int userId, int bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).
                orElseThrow(() -> new NotFoundException("Бронирования с таким id не существует"));
        if (booking.getItem() != null) {
            Item item = booking.getItem();
            if (item.getOwner().getId() == userId) {
                if (approved) {
                    if (!booking.getStatus().equals(Status.APPROVED)) {
                        booking.setStatus(Status.APPROVED);
                        bookingRepository.save(booking);
                    } else {
                        throw new WrongCommandException("Уже апрувнуто");
                    }
                } else {
                    if (!booking.getStatus().equals(Status.REJECTED)) {
                        booking.setStatus(Status.REJECTED);
                        bookingRepository.save(booking);
                    } else {
                        throw new WrongCommandException("Уже реджекнуто");
                    }
                }
            } else {
                throw new NotFoundException("Нет доступа на изменение - не владелец");
            }
        } else {
            throw new NotFoundException("Такого Item не существует");
        }
        return toBookingDtoWithItemAndBooker(booking);
    }

    @Override
    public BookingOutputDto getBookingById(int userId, int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирования с таким id не существует"));
        if (booking.getItem() != null) {
            Item item = booking.getItem();
            if (booking.getBooker().getId() == userId || item.getOwner().getId() == userId) {
                return toBookingDtoWithItemAndBooker(booking);
            } else {
                throw new NotFoundException("Бронирование недоступно");
            }
        }
        throw new NotFoundException("Вещь не найдена");
    }

    @Override
    public List<BookingOutputDto> getBookingByUser(int userId, State state, Integer from, Integer size) {
        if (userRepository.existsById(userId)) {
            if (from != null && size != null) {
                if (from >= 0 && size > 0) {
                    Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").descending());
                    if (state.equals(State.ALL)) {
                        return bookingRepository.findAllByBookerIdOrderByStartDesc(pageable, userId).stream()
                                .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                    } else if (state.equals(State.CURRENT)) {
                        return bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(pageable, userId, LocalDateTime.now(),
                                        LocalDateTime.now()).stream()
                                .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                    } else if (state.equals(State.PAST)) {
                        return bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(pageable, userId, LocalDateTime.now()).stream()
                                .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                    } else if (state.equals(State.FUTURE)) {
                        return bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(pageable, userId, LocalDateTime.now()).stream()
                                .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                    } else if (state.equals(State.WAITING)) {
                        return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(pageable, userId, Status.WAITING).stream()
                                .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                    } else if (state.equals(State.REJECTED)) {
                        return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(pageable, userId, Status.REJECTED).stream()
                                .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                    } else {
                        throw new WrongStateException(String.format("Unknown state: %s", state));
                    }
                } else {
                    throw new WrongCommandException("Неправильный запрос from и size");
                }
            } else {
                if (state.equals(State.ALL)) {
                    return bookingRepository.findAllByBookerIdOrderByStartDesc(userId).stream()
                            .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                } else if (state.equals(State.CURRENT)) {
                    return bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(),
                                    LocalDateTime.now()).stream()
                            .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                } else if (state.equals(State.PAST)) {
                    return bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()).stream()
                            .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                } else if (state.equals(State.FUTURE)) {
                    return bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()).stream()
                            .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                } else if (state.equals(State.WAITING)) {
                    return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING).stream()
                            .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                } else if (state.equals(State.REJECTED)) {
                    return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED).stream()
                            .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                } else {
                    throw new WrongStateException(String.format("Unknown state: %s", state));
                }
            }
        } else {
            throw new NotFoundException("Пользователя не существует");
        }
    }

    @Override
    public List<BookingOutputDto> getBookingByOwner(int userId, State state, Integer from, Integer size) {
        if (userRepository.existsById(userId)) {
            if (itemRepository.findByOwnerId(userId).size() > 0) {
                if (from != null && size != null) {
                    if (from >= 0 && size > 0) {
                        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").descending());
                        if (state.equals(State.ALL)) {
                            return bookingRepository.findByItemOwnerIdOrderByStartDesc(pageable, userId).stream()
                                    .map(BookingMapper::toBookingDtoWithItemAndBooker)
                                    .collect(Collectors.toList());
                        } else if (state.equals(State.CURRENT)) {
                            return bookingRepository.findCurrentByOwner(pageable, userId, LocalDateTime.now()).stream()
                                    .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                        } else if (state.equals(State.PAST)) {
                            return bookingRepository.findBookingsByOwnerInPast(pageable, userId, LocalDateTime.now()).stream()
                                    .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                        } else if (state.equals(State.FUTURE)) {
                            return bookingRepository.findBookingsByOwnerInFuture(pageable, userId, LocalDateTime.now()).stream()
                                    .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                        } else if (state.equals(State.WAITING)) {
                            return bookingRepository.findBookingsByOwnerAndStatus(pageable, userId, Status.WAITING).stream()
                                    .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                        } else if (state.equals(State.REJECTED)) {
                            return bookingRepository.findBookingsByOwnerAndStatus(pageable, userId, Status.REJECTED).stream()
                                    .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                        } else {
                            throw new WrongStateException(String.format("Unknown state: %s", state));
                        }
                    } else {
                        throw new WrongCommandException("Неправильный запрос from и size");
                    }
                } else {
                    if (state.equals(State.ALL)) {
                        return bookingRepository.findByItemOwnerIdOrderByStartDesc(userId).stream()
                                .map(BookingMapper::toBookingDtoWithItemAndBooker)
                                .collect(Collectors.toList());
                    } else if (state.equals(State.CURRENT)) {
                        return bookingRepository.findCurrentByOwner(userId, LocalDateTime.now()).stream()
                                .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                    } else if (state.equals(State.PAST)) {
                        return bookingRepository.findBookingsByOwnerInPast(userId, LocalDateTime.now()).stream()
                                .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                    } else if (state.equals(State.FUTURE)) {
                        return bookingRepository.findBookingsByOwnerInFuture(userId, LocalDateTime.now()).stream()
                                .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                    } else if (state.equals(State.WAITING)) {
                        return bookingRepository.findBookingsByOwnerAndStatus(userId, Status.WAITING).stream()
                                .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                    } else if (state.equals(State.REJECTED)) {
                        return bookingRepository.findBookingsByOwnerAndStatus(userId, Status.REJECTED).stream()
                                .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                    } else {
                        throw new WrongStateException(String.format("Unknown state: %s", state));
                    }
                }
            } else {
                throw new NotFoundException("Нет таких вещей");
            }
        } else {
            throw new NotFoundException("Пользователя не существует");
        }
    }
}
