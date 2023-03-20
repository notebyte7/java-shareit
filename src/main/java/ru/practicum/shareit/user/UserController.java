package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;

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
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PatchMapping(value = "/{id}")
    @ResponseBody
    public User updateUser(@RequestParam int id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @GetMapping
    @ResponseBody
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
