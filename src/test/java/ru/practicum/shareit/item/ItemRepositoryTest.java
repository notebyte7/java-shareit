package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    ItemRepository itemRepository;
    ItemDto itemDto;
    Item item;
    ItemRequestDto requestDto;
    ItemRequest request;
    UserDto ownerDto;
    User owner;


    @BeforeEach
    void setUp() {
        ownerDto = new UserDto(1, "user", "user@user.ru");
        owner = savedUser("user", "user@user.ru");

        User requestor = savedUser("requestor", "requestor@user.ru");

        request = new ItemRequest();
        request.setRequestor(requestor);
        request.setDescription("Description");
        request.setCreated(LocalDateTime.now());
        em.persist(request);
        em.flush();

        item = new Item();
        item.setDescription("Description");
        item.setName("Item");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);
        em.persist(item);
        em.flush();
    }

    @Test
    void findByRequestId() {
        List<Item> items = itemRepository.findByRequestId(request.getId());
        Item receivedItem = items.get(0);

        assertThat(items, hasSize(1));
        assertThat(receivedItem.getId(), equalTo(item.getId()));
        assertThat(receivedItem.getName(), equalTo(item.getName()));
        assertThat(receivedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(receivedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(receivedItem.getRequest(), equalTo(item.getRequest()));
    }

    @Test
    void findByOwner_Id() {
        List<Item> items = itemRepository.findByOwner_Id(owner.getId());
        Item receivedItem = items.get(0);

        assertThat(items.size(), equalTo(1));
        assertThat(receivedItem.getId(), equalTo(item.getId()));
        assertThat(receivedItem.getOwner(), equalTo(item.getOwner()));
        assertThat(receivedItem.getName(), equalTo(item.getName()));
        assertThat(receivedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(receivedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(receivedItem.getRequest(), equalTo(item.getRequest()));
    }

    @Test
    void search() {
        List<Item> items = itemRepository.search("Item");
        Item receivedItem = items.get(0);

        assertThat(items.size(), equalTo(1));
        assertThat(receivedItem.getId(), equalTo(item.getId()));
        assertThat(receivedItem.getOwner(), equalTo(item.getOwner()));
        assertThat(receivedItem.getName(), equalTo(item.getName()));
        assertThat(receivedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(receivedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(receivedItem.getRequest(), equalTo(item.getRequest()));

        items = itemRepository.search("Description");
        receivedItem = items.get(0);

        assertThat(items.size(), equalTo(1));
        assertThat(receivedItem.getId(), equalTo(item.getId()));
        assertThat(receivedItem.getOwner(), equalTo(item.getOwner()));
        assertThat(receivedItem.getName(), equalTo(item.getName()));
        assertThat(receivedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(receivedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(receivedItem.getRequest(), equalTo(item.getRequest()));
    }

    private User savedUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        em.persist(user);
        em.flush();
        return user;
    }
}