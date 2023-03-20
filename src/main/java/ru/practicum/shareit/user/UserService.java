package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;

import java.util.Collection;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        verifyUser(user);
        return userStorage.createUser(user);
    }

    public User updateUser(int id, User user) {
        if (isExist(id)) {
            verifyUser(id, user);
            return userStorage.updateUser(id, user);
        } else {
            throw new NotFoundException("Update User not found");
        }
    }

    public User getUserById(int id) {
        User newUser = userStorage.getUserById(id);
        if (newUser != null) {
            return newUser;
        } else {
            throw new NotFoundException("User by Id not found");
        }
    }

    private boolean isExist(int id) {
        return userStorage.getUserById(id) != null;

    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    private void verifyUser(User user) {
        userStorage.verifyUser(user);
    }

    private void verifyUser(int id, User user) {
        user.setId(id);
        userStorage.verifyUser(user);
    }
}
