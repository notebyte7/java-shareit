package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserStorage {
    User createUser(User user);

    User updateUser(int id, User user);

    User getUserById(int id);

    Collection<User> getAllUsers();

    void verifyUser(User user);

    boolean deleteUser(int id);
}
