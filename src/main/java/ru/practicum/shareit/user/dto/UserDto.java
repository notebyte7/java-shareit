package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    @NotBlank(message = "Поле name не должно быть пустым")
    private final String name;
    @Email(message = "Поле email неправильное")
    @NotBlank(message = "Поле email не должно быть пустым")
    private final String email;
}
