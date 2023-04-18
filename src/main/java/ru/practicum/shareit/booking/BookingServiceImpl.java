package ru.practicum.shareit.booking;

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
        if (itemRepository.findById(bookingDto.getItemId()).isPresent() && userRepository.findById(bookerId).isPresent()) {
            Booking booking = toBooking(bookingDto, itemRepository.findById(bookingDto.getItemId()).get(),
                    userRepository.findById(bookerId).get());
            if (booking.getItem().getOwner().getId() != bookerId) {
                if (booking.getStart() != null && booking.getEnd() != null) {
                    if (booking.getItem().getAvailable().equals(true)) {
                        if (booking.getStart().isAfter(LocalDateTime.now()) && booking.getStart().isBefore(booking.getEnd())
                                && !booking.getStart().isEqual(booking.getEnd())) {
                            booking.setStatus(Status.WAITING);

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
        } else {
            throw new NotFoundException("Объект не найден");
        }

    }

    @Override
    public BookingOutputDto approvingBooking(int userId, int bookingId, boolean approved) {
        if (bookingRepository.findById(bookingId).isPresent()) {
            Booking booking = bookingRepository.findById(bookingId).get();
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
        } else {
            throw new NotFoundException("Бронирования с таким id не существует");
        }
    }

    @Override
    public BookingOutputDto getBookingById(int userId, int bookingId) {
        if (bookingRepository.findById(bookingId).isPresent()) {
            Booking booking = bookingRepository.findById(bookingId).get();
            if (booking.getItem() != null) {
                Item item = booking.getItem();
                if (booking.getBooker().getId() == userId || item.getOwner().getId() == userId) {
                    return toBookingDtoWithItemAndBooker(booking);
                } else {
                    throw new NotFoundException("Бронирование недоступно");
                }
            }
            throw new NotFoundException("Вещь не найдена");
        } else {
            throw new NotFoundException("Бронирования не существует");
        }
    }

    @Override
    public List<BookingOutputDto> getBookingByUser(int userId, State state) {
        if (userRepository.existsById(userId)) {
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
        } else {
            throw new NotFoundException("Пользователя не существует");
        }
    }

    @Override
    public List<BookingOutputDto> getBookingByOwner(int userId, State state) {
        if (userRepository.existsById(userId)) {
            if (itemRepository.findByOwner_Id(userId).size() > 0) {
                if (state.equals(State.ALL)) {
                    return bookingRepository.findByItem_Owner_IdOrderByStartDesc(userId).stream()
                            .map(BookingMapper::toBookingDtoWithItemAndBooker)
                            .collect(Collectors.toList());
                } else if (state.equals(State.CURRENT)) {
                    return bookingRepository.findCurrentByOwner(userId, LocalDateTime.now()).stream()
                            .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                } else if (state.equals(State.PAST)) {
                    return bookingRepository.findBookingsByOwnItemByUserAndPast(userId, LocalDateTime.now()).stream()
                            .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                } else if (state.equals(State.FUTURE)) {
                    return bookingRepository.findBookingsByOwnItemByUserAndFuture(userId, LocalDateTime.now()).stream()
                            .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                } else if (state.equals(State.WAITING)) {
                    return bookingRepository.findBookingsByOwnItemByUserAndStatus(userId, Status.WAITING).stream()
                            .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                } else if (state.equals(State.REJECTED)) {
                    return bookingRepository.findBookingsByOwnItemByUserAndStatus(userId, Status.REJECTED).stream()
                            .map(BookingMapper::toBookingDtoWithItemAndBooker).collect(Collectors.toList());
                } else {
                    throw new WrongStateException(String.format("Unknown state: %s", state));
                }
            } else {
                throw new NotFoundException("Нет таких вещей");
            }
        } else {
            throw new NotFoundException("Пользователя не существует");
        }
    }
}
