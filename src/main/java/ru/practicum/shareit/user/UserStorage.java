package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    UserDto createUser(User user);

    UserDto updateUser(int id, User user);

    Collection<UserDto> getAllUsers();

    void verifyUser(int id, User user);

    void verifyUser(User user);

    boolean deleteUser(int id);

    UserDto getUserDtoById(int id);
}
