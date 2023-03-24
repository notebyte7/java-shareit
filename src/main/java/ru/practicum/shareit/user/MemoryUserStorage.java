package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.EmailExistsException;
import ru.practicum.shareit.exeption.NotFoundException;

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
        verifyUser(user);
        User updatedUser = getUserById(id);
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        return updatedUser;
    }

    private void verifyUserEmail(int id, String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email) && user.getId() != id) {
                throw new EmailExistsException("This email already exists");
            }
        }
    }

    @Override
    public User getUserById(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("User by Id not found");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public boolean deleteUser(int id) {
        users.remove(id);
        return true;
    }

    @Override
    public void verifyUser(User user) {
        verifyUserEmail(user.getId(), user.getEmail());
    }

    private int generateId() {
        return ++uid;
    }
}
