package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseBody
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PatchMapping(value = "/{userId}")
    @ResponseBody
    public User updateUser(@RequestBody User user, @PathVariable int userId) {
        return userService.updateUser(userId, user);
    }

    @GetMapping(value = "/{userId}")
    @ResponseBody
    public User getUserById(@PathVariable int userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    @ResponseBody
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping(value = "/{userId}")
    @ResponseBody
    public void deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
    }
}
