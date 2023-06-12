package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@PersistenceContext
class UserServiceIntegrationTest {

    final EntityManager em;
    final UserService userService;
    final UserDto userDto = makeUserDto("user", "user@user.ru");

    @Test
    void createUser() {
        UserDto newUserDto = userService.createUser(userDto);
        User user = em.find(User.class, newUserDto.getId());

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(newUserDto.getName()));
        assertThat(user.getEmail(), equalTo(newUserDto.getEmail()));
    }

    @Test
    void getUserById() {
        User user = savedUser();
        UserDto expectedUserDto = userService.getUserById(user.getId());

        assertThat(expectedUserDto.getId(), equalTo(user.getId()));
        assertThat(expectedUserDto.getName(), equalTo(user.getName()));
        assertThat(expectedUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void deleteUser() {
        UserDto newUserDto = userService.createUser(userDto);
        User user = em.find(User.class, newUserDto.getId());
        UserDto expectedUserDto = userService.getUserById(user.getId());

        assertThat(expectedUserDto.getId(), equalTo(user.getId()));
        assertThat(expectedUserDto.getName(), equalTo(user.getName()));
        assertThat(expectedUserDto.getEmail(), equalTo(user.getEmail()));

        userService.deleteUser(user.getId());
        User deletedUser = em.find(User.class, user.getId());

        assertThat(deletedUser, equalTo(null));

        assertThrows(NotFoundException.class,
                () -> userService.deleteUser(99));
    }

    @Test
    void getAllUsers() {
        Collection<UserDto> users = List.of(
                userDto,
                new UserDto(2, "user2", "user2@user.ru")
        );

        for (UserDto userDto : users) {
            userService.createUser(userDto);
        }

        Collection<UserDto> expectedUsers = userService.getAllUsers();

        assertThat(expectedUsers, hasSize(users.size()));
        for (UserDto userDto : expectedUsers) {
            assertThat(expectedUsers, hasItem(allOf(
                    hasProperty("id", equalTo(userDto.getId())),
                    hasProperty("name", equalTo(userDto.getName())),
                    hasProperty("email", equalTo(userDto.getEmail()))
            )));
        }
    }

    @Test
    void updateUser() {
        User user = savedUser();

        UserDto userDtoUpdated = new UserDto(1, "New User", "new@user.ru");

        UserDto patchedUser = userService.updateUser(user.getId(), userDtoUpdated);

        assertThat(patchedUser.getId(), equalTo(user.getId()));
        assertThat(patchedUser.getName(), equalTo(userDtoUpdated.getName()));
        assertThat(patchedUser.getEmail(), equalTo(userDtoUpdated.getEmail()));
    }

    private User savedUser() {
        User user = UserMapper.toUser(userDto);
        em.persist(user);
        em.flush();
        return user;
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto userDto = new UserDto(null, name, email);
        return userDto;
    }
}
