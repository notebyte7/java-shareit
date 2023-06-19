package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

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
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PatchMapping(value = "/{userId}")
    @ResponseBody
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable int userId) {
        return userService.updateUser(userId, userDto);
    }

    @GetMapping(value = "/{userId}")
    @ResponseBody
    public UserDto getUserById(@PathVariable int userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    @ResponseBody
    public Collection<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping(value = "/{userId}")
    @ResponseBody
    public void deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
    }
}
