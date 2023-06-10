package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.UserMapper.toUser;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(UserDto userDto) {
        User user = toUser(userDto);
        User user1 = userRepository.save(user);
        UserDto userDto1 = toUserDto(user1);
        return userDto1;
    }

    public UserDto updateUser(int id, UserDto userDto) {
        if (userRepository.findById(id).isPresent()) {
            User user = toUser(userDto);
            User updatedUser = userRepository.findById(id).get();
            if (user.getName() != null) {
                updatedUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                updatedUser.setEmail(user.getEmail());
            }
            user = userRepository.save(updatedUser);
            return toUserDto(user);
        } else {
            throw new NotFoundException("Update User not found");
        }
    }

    public UserDto getUserById(int id) {
        if (userRepository.findById(id).isPresent()) {
            return toUserDto(userRepository.findById(id).get());
        } else {
            throw new NotFoundException("User by Id not found");
        }
    }

    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new NotFoundException("User by Id not found");
        }
    }
}
