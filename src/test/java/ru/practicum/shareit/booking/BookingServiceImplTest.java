package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.StatusDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.WrongCommandException;
import ru.practicum.shareit.exeption.WrongStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    final EntityManager entityManager;
    final BookingServiceImpl bookingService;
    User owner;
    User booker;
    Item item;
    BookingOutputDto firstBooking;
    BookingOutputDto secondBooking;


    @BeforeEach
    void setUp() {
        owner = setUser("owner", "owner@user.ru");
        booker = setUser("booker", "booker@user.ru");

        item = new Item();
        item.setOwner(owner);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        entityManager.persist(item);
        entityManager.flush();

        firstBooking = bookingService.createBooking(
                booker.getId(),
                setBookingDto(
                        item.getId(),
                        LocalDateTime.of(2023, Month.JUNE, 15, 12, 0, 0),
                        LocalDateTime.of(2023, Month.JUNE, 20, 12, 0, 0)));

        secondBooking = bookingService.createBooking(
                booker.getId(),
                setBookingDto(
                        item.getId(),
                        LocalDateTime.of(2023, Month.JULY, 1, 12, 0, 0),
                        LocalDateTime.of(2023, Month.JULY, 2, 12, 0, 0)));
    }

    @Test
    void createBooking() {
        assertThat(firstBooking.getId(), notNullValue());
        assertThat(firstBooking.getStart(), equalTo(LocalDateTime.of(2023, Month.JUNE, 15, 12, 0, 0)));
        assertThat(firstBooking.getEnd(), equalTo(LocalDateTime.of(2023, Month.JUNE, 20, 12, 0, 0)));
        assertThat(firstBooking.getStatus().toString(), equalTo("WAITING"));
        assertThat(firstBooking.getBooker().getId(), equalTo(booker.getId()));
        assertThat(firstBooking.getItem().getId(), equalTo(item.getId()));

        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(booker.getId(), new BookingDto(1, LocalDateTime.now(), LocalDateTime.now(), 99, 1, StatusDto.APPROVED)));
        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(owner.getId(), new BookingDto(1, LocalDateTime.now(), LocalDateTime.now(), 99, 1, StatusDto.APPROVED)));
        assertThrows(WrongCommandException.class,
                () -> bookingService.createBooking(booker.getId(), new BookingDto(1, null, LocalDateTime.now(), 1, 1, StatusDto.APPROVED)));
        assertThrows(WrongCommandException.class,
                () -> bookingService.createBooking(booker.getId(), new BookingDto(1, LocalDateTime.now(), null, 1, 1, StatusDto.APPROVED)));
        assertThrows(WrongCommandException.class,
                () -> bookingService.createBooking(booker.getId(), new BookingDto(1, LocalDateTime.now().minusHours(1), LocalDateTime.now(), 1, 1, StatusDto.APPROVED)));
        item.setAvailable(false);
        assertThrows(WrongCommandException.class,
                () -> bookingService.createBooking(booker.getId(), new BookingDto(1, LocalDateTime.now(), LocalDateTime.now(), 1, 1, StatusDto.APPROVED)));


    }

    @Test
    void approvingBooking() {
        BookingOutputDto booking = bookingService.getBookingById(booker.getId(), firstBooking.getId());

        assertThat(booking.getStatus().toString(), equalTo("WAITING"));

        bookingService.approvingBooking(owner.getId(), firstBooking.getId(), true);
        booking = bookingService.getBookingById(booker.getId(), firstBooking.getId());

        assertThat(booking.getStatus().toString(), equalTo("APPROVED"));

        assertThrows(WrongCommandException.class,
                () -> bookingService.approvingBooking(owner.getId(), firstBooking.getId(), true));

        bookingService.approvingBooking(owner.getId(), firstBooking.getId(), false);
        booking = bookingService.getBookingById(booker.getId(), firstBooking.getId());
        assertThat(booking.getStatus().toString(), equalTo("REJECTED"));

        assertThrows(WrongCommandException.class,
                () -> bookingService.approvingBooking(owner.getId(), firstBooking.getId(), false));

        assertThrows(NotFoundException.class,
                () -> bookingService.approvingBooking(booker.getId(), firstBooking.getId(), true));

        assertThrows(NotFoundException.class,
                () -> bookingService.approvingBooking(owner.getId(), 99, false));

    }

    @Test
    void getBookingById() {
        BookingOutputDto booking = bookingService.getBookingById(booker.getId(), firstBooking.getId());

        assertThat(booking.getId(), equalTo(firstBooking.getId()));
        assertThat(booking.getStart(), equalTo(firstBooking.getStart()));
        assertThat(booking.getEnd(), equalTo(firstBooking.getEnd()));
        assertThat(booking.getStatus(), equalTo(firstBooking.getStatus()));
        assertThat(booking.getBooker().getId(), equalTo(firstBooking.getBooker().getId()));
        assertThat(booking.getItem().getId(), equalTo(firstBooking.getItem().getId()));
    }

    @Test
    void getBookingByUser() {
        BookingOutputDto receivedBooking = bookingService.getBookingById(booker.getId(), secondBooking.getId());

        assertThat(receivedBooking.getId(), equalTo(secondBooking.getId()));
        assertThat(receivedBooking.getStart(), equalTo(secondBooking.getStart()));
        assertThat(receivedBooking.getEnd(), equalTo(secondBooking.getEnd()));
        assertThat(receivedBooking.getStatus(), equalTo(secondBooking.getStatus()));
        assertThat(receivedBooking.getBooker().getId(), equalTo(secondBooking.getBooker().getId()));
        assertThat(receivedBooking.getItem().getId(), equalTo(secondBooking.getItem().getId()));

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(99, firstBooking.getId()));

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(1, 99));

        List<BookingOutputDto> bookings = bookingService.getBookingByUser(booker.getId(), State.ALL, 0, 10);

        assertThat(bookings, hasSize(2));
        assertThat(bookings.get(0).getId(), equalTo(secondBooking.getId()));
        assertThat(bookings.get(1).getId(), equalTo(firstBooking.getId()));

        bookings = bookingService.getBookingByUser(booker.getId(), State.CURRENT, 0, 10);
        assertThat(bookings, hasSize(0));

        bookings = bookingService.getBookingByUser(booker.getId(), State.PAST, 0, 10);
        assertThat(bookings, hasSize(0));

        bookings = bookingService.getBookingByUser(booker.getId(), State.FUTURE, 0, 10);
        assertThat(bookings, hasSize(2));

        bookings = bookingService.getBookingByUser(booker.getId(), State.WAITING, 0, 10);
        assertThat(bookings, hasSize(2));

        bookings = bookingService.getBookingByUser(booker.getId(), State.REJECTED, 0, 10);
        assertThat(bookings, hasSize(0));

        assertThrows(WrongCommandException.class,
                () -> bookingService.getBookingByUser(booker.getId(), State.ALL, 0, 0));


        bookings = bookingService.getBookingByUser(booker.getId(), State.ALL, null, null);

        assertThat(bookings, hasSize(2));
        assertThat(bookings.get(0).getId(), equalTo(secondBooking.getId()));
        assertThat(bookings.get(1).getId(), equalTo(firstBooking.getId()));

        bookings = bookingService.getBookingByUser(booker.getId(), State.CURRENT, null, null);
        assertThat(bookings, hasSize(0));

        bookings = bookingService.getBookingByUser(booker.getId(), State.PAST, null, null);
        assertThat(bookings, hasSize(0));

        bookings = bookingService.getBookingByUser(booker.getId(), State.FUTURE, null, null);
        assertThat(bookings, hasSize(2));

        bookings = bookingService.getBookingByUser(booker.getId(), State.WAITING, null, null);
        assertThat(bookings, hasSize(2));

        bookings = bookingService.getBookingByUser(booker.getId(), State.REJECTED, null, null);
        assertThat(bookings, hasSize(0));
    }

    @Test
    void getBookingByOwner() {
        List<BookingOutputDto> bookings = bookingService.getBookingByOwner(owner.getId(), State.ALL, 0, 10);

        assertThat(bookings, hasSize(2));
        assertThat(bookings.get(0).getId(), equalTo(secondBooking.getId()));
        assertThat(bookings.get(1).getId(), equalTo(firstBooking.getId()));

        bookings = bookingService.getBookingByOwner(owner.getId(), State.CURRENT, 0, 10);
        assertThat(bookings, hasSize(0));

        bookings = bookingService.getBookingByOwner(owner.getId(), State.PAST, 0, 10);
        assertThat(bookings, hasSize(0));

        bookings = bookingService.getBookingByOwner(owner.getId(), State.FUTURE, 0, 10);
        assertThat(bookings, hasSize(2));

        bookings = bookingService.getBookingByOwner(owner.getId(), State.WAITING, 0, 10);
        assertThat(bookings, hasSize(2));

        bookings = bookingService.getBookingByOwner(owner.getId(), State.REJECTED, 0, 10);
        assertThat(bookings, hasSize(0));


        bookings = bookingService.getBookingByOwner(owner.getId(), State.ALL, null, null);

        assertThat(bookings, hasSize(2));
        assertThat(bookings.get(0).getId(), equalTo(secondBooking.getId()));
        assertThat(bookings.get(1).getId(), equalTo(firstBooking.getId()));

        bookings = bookingService.getBookingByOwner(owner.getId(), State.CURRENT, null, null);
        assertThat(bookings, hasSize(0));

        bookings = bookingService.getBookingByOwner(owner.getId(), State.PAST, null, null);
        assertThat(bookings, hasSize(0));

        bookings = bookingService.getBookingByOwner(owner.getId(), State.FUTURE, null, null);
        assertThat(bookings, hasSize(2));

        bookings = bookingService.getBookingByOwner(owner.getId(), State.WAITING, null, null);
        assertThat(bookings, hasSize(2));

        bookings = bookingService.getBookingByOwner(owner.getId(), State.REJECTED, null, null);
        assertThat(bookings, hasSize(0));

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingByOwner(booker.getId(), State.ALL, 0, 0));

        assertThrows(WrongStateException.class,
                () -> bookingService.getBookingByOwner(owner.getId(), State.valueOf("AAA"), 0, 10));

    }

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    private BookingDto setBookingDto(Integer itemId, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        return BookingMapper.toBookingDto(booking);
    }
}