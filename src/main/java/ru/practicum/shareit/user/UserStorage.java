package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User createUser(User user);

    User updateUser(int id, User user);

    Collection<User> getAllUsers();

    User getUserById(int id);

    boolean deleteUser(int id);
}
