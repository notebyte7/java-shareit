package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class MemoryUserStorage implements UserStorage {
    private int uid;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        int id = generateId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(int id, User user) {
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
        return updatedUser;
    }

    public User getUserById(int id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean deleteUser(int id) {
        users.remove(id);
        return true;
    }

    private int generateId() {
        return ++uid;
    }
}
