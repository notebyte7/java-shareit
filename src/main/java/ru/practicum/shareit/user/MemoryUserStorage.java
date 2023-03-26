package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.EmailExistsException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Component
public class MemoryUserStorage implements UserStorage {
    private int uid;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public UserDto createUser(User user) {
        int id = generateId();
        user.setId(id);
        users.put(id, user);
        return toUserDto(user);
    }

    @Override
    public UserDto updateUser(int id, User user) {
        User updatedUser = getUserById(id);
        if (updatedUser != null) {
            if (user.getName() != null) {
                updatedUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                updatedUser.setEmail(user.getEmail());
            }
        }
        users.put(id, updatedUser);
        return toUserDto(updatedUser);
    }

    private void verifyUserEmailWithId(int id, String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email) && user.getId() != id) {
                throw new EmailExistsException("This email already exists");
            }
        }
    }

    private void verifyUserEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                throw new EmailExistsException("This email already exists");
            }
        }
    }

    private User getUserById(int id) {
        if (users.containsKey(id)) {
            User user = users.get(id);
            return user;
        } else {
            throw new NotFoundException("User by Id not found");
        }
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteUser(int id) {
        users.remove(id);
        return true;
    }

    @Override
    public UserDto getUserDtoById(int id) {
        User user = getUserById(id);
        return toUserDto(user);
    }

    @Override
    public void verifyUser(int id, User user) {
        verifyUserEmailWithId(id, user.getEmail());
    }

    @Override
    public void verifyUser(User user) {
        verifyUserEmail(user.getEmail());
    }

    private int generateId() {
        return ++uid;
    }
}
