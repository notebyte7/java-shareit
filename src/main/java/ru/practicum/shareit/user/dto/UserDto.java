package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
public class UserDto {
    Integer id;
    @NotBlank(message = "Поле name не должно быть пустым")
    String name;
    @Email(message = "Поле email неправильное")
    @NotBlank(message = "Поле email не должно быть пустым")
    String email;
}

