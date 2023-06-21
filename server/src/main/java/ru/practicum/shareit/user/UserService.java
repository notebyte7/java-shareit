package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(int id, UserDto userDto);

    UserDto getUserById(int id);

    Collection<UserDto> getAllUsers();

    void deleteUser(int id);
}
