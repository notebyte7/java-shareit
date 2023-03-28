package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.EmailExistsException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.UserMapper.toUser;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(UserDto userDto) {
        User user = toUser(userDto);
        verifyUser(user);
        return toUserDto(userStorage.createUser(user));
    }

    public UserDto updateUser(int id, UserDto userDto) {
        if (isExist(id)) {
            User user = toUser(userDto);
            verifyUser(id, user);
            return toUserDto(userStorage.updateUser(id, user));
        } else {
            throw new NotFoundException("Update User not found");
        }
    }

    public UserDto getUserById(int id) {
        if (isExist(id)) {
            return toUserDto(userStorage.getUserById(id));
        } else {
            throw new NotFoundException("User by Id not found");
        }
    }

    private boolean isExist(int id) {
        return userStorage.getUserById(id) != null;
    }

    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private void verifyUser(int id, User user) {
        boolean verify = getAllUsers().stream()
                .noneMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()) && u.getId() != id);
        if (!verify) {
            throw new EmailExistsException("This email already exists");
        }
    }

    private void verifyUser(User user) {
        boolean verify = getAllUsers().stream()
                .noneMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));
        if (!verify) {
            throw new EmailExistsException("This email already exists");
        }
    }

    public boolean deleteUser(int id) {
        if (isExist(id)) {
            return userStorage.deleteUser(id);
        } else {
            throw new NotFoundException("User by Id not found");
        }
    }
}
