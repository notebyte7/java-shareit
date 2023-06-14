package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exeption.ForbiddenException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.WrongCommandException;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {
    final EntityManager em;
    final ItemService itemService;
    Item item;
    ItemDto itemDto;
    User owner;
    User booker;
    Booking lastBooking;
    Booking nextBooking;
    Comment comment;
    ItemRequest request;


    @BeforeEach
    void setUp() {
        owner = setUser("user", "user@user.ru");
        booker = setUser("booker", "booker@user.ru");
        itemDto = itemService.createItem(owner.getId(), ItemMapper.toItemDto(setItem(setRequest().getId())));
        lastBooking = setBooking(
                booker,
                LocalDateTime.of(2023, Month.MAY, 10, 12, 0, 0),
                LocalDateTime.of(2023, Month.MAY, 11, 12, 0, 0),
                Status.WAITING);
        setBooking(
                booker,
                LocalDateTime.of(2023, Month.APRIL, 1, 12, 0, 0),
                LocalDateTime.of(2023, Month.APRIL, 2, 12, 0, 0),
                Status.WAITING);
        setBooking(
                booker,
                LocalDateTime.of(2023, Month.MAY, 12, 12, 0, 0),
                LocalDateTime.of(2023, Month.MAY, 13, 12, 0, 0),
                Status.REJECTED);
        nextBooking = setBooking(
                booker,
                LocalDateTime.of(2024, Month.MAY, 10, 12, 0, 0),
                LocalDateTime.of(2024, Month.MAY, 11, 12, 0, 0),
                Status.WAITING);
        setBooking(
                booker,
                LocalDateTime.of(2024, Month.JUNE, 12, 12, 0, 0),
                LocalDateTime.of(2024, Month.JUNE, 13, 12, 0, 0),
                Status.WAITING);
        setBooking(
                booker,
                LocalDateTime.of(2024, Month.APRIL, 12, 12, 0, 0),
                LocalDateTime.of(2024, Month.APRIL, 13, 12, 0, 0), Status.REJECTED);
        comment = setComment();
        request = setRequest();
    }

    @Test
    void createItem() {
        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo("Item"));
        assertThat(itemDto.getDescription(), equalTo("Description"));
        assertThat(itemDto.getAvailable(), equalTo(true));
        assertThat(itemDto.getRequestId(), equalTo(null));


        assertThrows(NotFoundException.class,
                () -> itemService.createItem(99, ItemMapper.toItemDto(setItem(setRequest().getId()))));

    }

    @Test
    void updateItem() {
        ItemDto updatedItem = new ItemDto(itemDto.getId(), "New Item", "new Description", false, request.getId());
        ItemDto patchedItem = itemService.updateItem(owner.getId(), itemDto.getId(), updatedItem);

        assertThat(patchedItem.getId(), equalTo(itemDto.getId()));
        assertThat(patchedItem.getName(), equalTo("New Item"));
        assertThat(patchedItem.getDescription(), equalTo("new Description"));
        assertThat(patchedItem.getAvailable(), equalTo(false));

        assertThrows(ForbiddenException.class,
                () -> itemService.updateItem(booker.getId(), itemDto.getId(), updatedItem));
        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(booker.getId(), 99, updatedItem));

    }

    @Test
    void getItemDtoById() {
        ItemOutputDto receivedItem = itemService.getItemDtoById(owner.getId(), itemDto.getId());

        assertThat(receivedItem.getId(), equalTo(itemDto.getId()));
        assertThat(receivedItem.getOwnerId(), equalTo(owner.getId()));
        assertThat(receivedItem.getName(), equalTo(itemDto.getName()));
        assertThat(receivedItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(receivedItem.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(receivedItem.getLastBooking(), equalTo(BookingMapper.toBookingShortForItem(lastBooking)));
        assertThat(receivedItem.getNextBooking(), equalTo(BookingMapper.toBookingShortForItem(nextBooking)));

        assertThrows(NotFoundException.class,
                () -> itemService.getItemDtoById(1, 99));

        receivedItem = itemService.getItemDtoById(booker.getId(), itemDto.getId());

        assertThat(receivedItem.getId(), equalTo(itemDto.getId()));
        assertThat(receivedItem.getOwnerId(), equalTo(owner.getId()));
        assertThat(receivedItem.getName(), equalTo(itemDto.getName()));
        assertThat(receivedItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(receivedItem.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(receivedItem.getLastBooking(), equalTo(null));
        assertThat(receivedItem.getNextBooking(), equalTo(null));


    }

    @Test
    void getItemsByOwner() {
        Collection<ItemOutputDto> items = itemService.getItemsByOwner(owner.getId(), 0, 5);
        ItemOutputDto item = items.iterator().next();

        assertThat(items, hasSize(1));
        assertThat(item.getId(), equalTo(itemDto.getId()));
        assertThat(item.getOwnerId(), equalTo(owner.getId()));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(item.getLastBooking(), equalTo(BookingMapper.toBookingShortForItem(lastBooking)));
        assertThat(item.getNextBooking(), equalTo(BookingMapper.toBookingShortForItem(nextBooking)));

        assertThrows(WrongCommandException.class,
                () -> itemService.getItemsByOwner(owner.getId(), 0, 0));

        items = itemService.getItemsByOwner(owner.getId(), null, null);
        item = items.iterator().next();

        assertThat(items, hasSize(1));
        assertThat(item.getId(), equalTo(itemDto.getId()));
        assertThat(item.getOwnerId(), equalTo(owner.getId()));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(item.getLastBooking(), equalTo(BookingMapper.toBookingShortForItem(lastBooking)));
        assertThat(item.getNextBooking(), equalTo(BookingMapper.toBookingShortForItem(nextBooking)));

    }

    @Test
    void searchItemsByText() {
        Collection<ItemDto> presentList = itemService.searchItemsByText("Item", 0, 10);
        assertThat(presentList, hasSize(1));
        assertThat(presentList, hasItem(itemDto));

        presentList = itemService.searchItemsByText("", 0, 10);
        assertThat(presentList, hasSize(0));

        assertThrows(WrongCommandException.class,
                () -> itemService.searchItemsByText("text", 0, 0));

        presentList = itemService.searchItemsByText("Item", null, null);
        assertThat(presentList, hasSize(1));
        assertThat(presentList, hasItem(itemDto));

        presentList = itemService.searchItemsByText("", null, null);
        assertThat(presentList, hasSize(0));
    }

    @Test
    void createComment() {
        Comment newComment = setComment();

        assertThrows(WrongCommandException.class,
                () -> itemService.createComment(booker.getId(), itemDto.getId(), CommentMapper.toCommentDto(newComment)));

        setBooking(booker, LocalDateTime.of(2023, Month.APRIL, 1, 12, 0, 0),
                LocalDateTime.of(2023, Month.APRIL, 2, 12, 0, 0), Status.APPROVED);
        CommentOutputDto postedComment = itemService.createComment(booker.getId(), itemDto.getId(), CommentMapper.toCommentDto(newComment));

        assertThat(postedComment.getId(), notNullValue());
        assertThat(postedComment.getAuthorName(), equalTo(newComment.getAuthor().getName()));
        assertThat(postedComment.getText(), equalTo(newComment.getText()));
        assertThat(postedComment.getCreated(), equalTo(newComment.getCreated()));

        assertThrows(WrongCommandException.class,
                () -> itemService.createComment(booker.getId(), 99, CommentMapper.toCommentDto(newComment)));

    }

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        em.persist(user);
        em.flush();
        return user;
    }

    private Item setItem(Integer requestId) {
        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setRequest(request);

        return item;
    }

    private Booking setBooking(User booker, LocalDateTime start, LocalDateTime end, Status status) {
        Booking booking = new Booking();
        booking.setItem(em.find(Item.class, itemDto.getId()));
        booking.setBooker(booker);
        booking.setStatus(status);
        booking.setStart(start);
        booking.setEnd(end);
        em.persist(booking);
        em.flush();
        return booking;
    }

    private Comment setComment() {
        Comment comment = new Comment();
        comment.setText("Comment");
        comment.setAuthor(booker);
        comment.setItemId(itemDto.getId());
        comment.setCreated(LocalDateTime.now());
        em.persist(comment);
        em.flush();
        return comment;
    }

    private ItemRequest setRequest() {
        ItemRequest request = new ItemRequest();
        request.setRequestor(booker);
        request.setDescription("Request");
        request.setCreated(LocalDateTime.now());
        em.persist(request);
        em.flush();
        return request;
    }
}