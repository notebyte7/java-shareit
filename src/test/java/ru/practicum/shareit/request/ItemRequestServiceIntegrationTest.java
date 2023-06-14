package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.WrongCommandException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@PersistenceContext
class ItemRequestServiceIntegrationTest {

    final EntityManager em;
    final ItemRequestService itemRequestService;
    User owner;
    User requester;
    ItemRequestDto request;
    Item item;

    @BeforeEach
    void setUp() {
        owner = setUser("Owner", "owner@email.com");
        requester = setUser("Requester", "requester@email.com");
        request = itemRequestService.createRequest(requester.getId(), ItemRequestMapper.toItemRequestDto(setRequest()));
        item = setItem(owner, em.find(ItemRequest.class, request.getId()));
        em.persist(item);
        em.flush();
    }

    @Test
    void createRequest() {
        assertThat(request.getId(), notNullValue());
        assertThat(request.getDescription(), equalTo("Request"));
        assertThat(request.getCreated(), notNullValue());
        assertThat(request.getItems(), equalTo(null));
    }

    @Test
    void getRequests() {
        Collection<ItemRequestDto> requestDtoList = itemRequestService.getRequests(requester.getId());
        ItemRequestDto itemRequestDto = requestDtoList.iterator().next();

        assertThat(requestDtoList.size(), equalTo(1));
        assertThat(itemRequestDto.getId(), equalTo(request.getId()));
        assertThat(itemRequestDto.getDescription(), equalTo(request.getDescription()));
        assertThat(itemRequestDto.getCreated(), equalTo(request.getCreated()));

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequests(99));
    }

    @Test
    void getRequests_byUserIdAndRequestId() {
        ItemRequestDto itemRequestDto = itemRequestService.getRequests(requester.getId(), request.getId());

        assertThat(itemRequestDto.getId(), equalTo(request.getId()));
        assertThat(itemRequestDto.getDescription(), equalTo(request.getDescription()));
        assertThat(itemRequestDto.getCreated(), equalTo(request.getCreated()));

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequests(99, request.getId()));

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequests(requester.getId(), 99));

    }

    @Test
    void getRequestsAll() {
        Collection<ItemRequestDto> requestDtoList = itemRequestService.getRequestsAll(owner.getId(), 0, 10);
        ItemRequestDto itemRequestDto = requestDtoList.iterator().next();

        assertThat(requestDtoList.size(), equalTo(1));
        assertThat(itemRequestDto.getId(), equalTo(request.getId()));
        assertThat(itemRequestDto.getDescription(), equalTo(request.getDescription()));
        assertThat(itemRequestDto.getCreated(), equalTo(request.getCreated()));

        assertThrows(WrongCommandException.class,
                () -> itemRequestService.getRequestsAll(requester.getId(), 0, 0));

        requestDtoList = itemRequestService.getRequestsAll(owner.getId(), null, null);
        assertThat(requestDtoList, equalTo(new ArrayList<>()));
    }

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        em.persist(user);
        em.flush();
        return user;
    }

    private ItemRequest setRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Request");
        return itemRequest;
    }

    private Item setItem(User owner, ItemRequest itemRequest) {
        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(itemRequest);
        return item;
    }
}