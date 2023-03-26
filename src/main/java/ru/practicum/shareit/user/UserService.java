package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static ru.practicum.shareit.user.UserMapper.toUser;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(UserDto userDto) {
        User user = toUser(userDto);
        verifyUser(user);
        return userStorage.createUser(user);
    }

    public UserDto updateUser(int id, UserDto userDto) {
        if (isExist(id)) {
            User user = toUser(userDto);
            verifyUser(id, user);
            return userStorage.updateUser(id, user);
        } else {
            throw new NotFoundException("Update User not found");
        }
    }

    public UserDto getUserById(int id) {
        if (isExist(id)) {
            UserDto newUserDto = userStorage.getUserDtoById(id);
            return newUserDto;
        } else {
            throw new NotFoundException("User by Id not found");
        }
    }

    private boolean isExist(int id) {
        return userStorage.getUserDtoById(id) != null;
    }

    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers();
    }

    private void verifyUser(int id, User user) {
        userStorage.verifyUser(id, user);
    }

    private void verifyUser(User user) {
        userStorage.verifyUser(user);
    }

    public boolean deleteUser(int id) {
        if (isExist(id)) {
            return userStorage.deleteUser(id);
        } else {
            throw new NotFoundException("User by Id not found");
        }
    }
}
