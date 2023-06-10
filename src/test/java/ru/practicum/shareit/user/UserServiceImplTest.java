package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    UserDto userDto;
    User user;
    User updatedUser;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1, "user", "user@user.ru");
        user = UserMapper.toUser(userDto);
        updatedUser = new User(1, "user", "user@user.ru");
    }

    @Test
    void createUser() throws Exception {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        Assertions.assertEquals(userService.createUser(userDto), userDto);
    }

    @Test
    void updateUser() {


        updatedUser = new User(1, "newuser", "newuser@user.ru");
        UserDto updatedUserDto = UserMapper.toUserDto(updatedUser);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(updatedUser);

        UserDto newUser = userService.updateUser(user.getId(), updatedUserDto);

        Assertions.assertEquals(newUser,
                UserMapper.toUserDto(updatedUser));
        Mockito.verify(userRepository, Mockito.times(2))
                .findById(anyInt());
        Mockito.verify(userRepository, Mockito.times(1))
                .save(any(User.class));
    }

    @Test
    void getUserById_whenUserFound_thenReturnedUser() {
        int userId = 1;
        User user1 = new User(1, "Test1", "test1@test.ru");
        UserDto userDto1 = UserMapper.toUserDto(user1);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user1));

        UserDto expectedUserDto = userService.getUserById(userId);

        assertEquals(userDto1, expectedUserDto);
    }

    @Test
    void getUserById_whenUserNotFound_thenUserNotFoundException() {
        int userId = 0;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));
        Assertions.assertEquals(userService.getAllUsers(), List.of(userDto));
    }

    @Test
    void deleteUser() {
        doNothing()
                .when(userRepository).deleteById(anyInt());
        userService.deleteUser(anyInt());
        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(anyInt());
    }
}