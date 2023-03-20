package ru.practicum.shareit.user;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    private int id;
    @NotBlank(message = "Поле name не должно быть пустым")
    private String name;
    @Email(message = "Поле email неправильное")
    @NotBlank(message = "Поле email не должно быть пустым")
§    @NonNull
    private String email;
}
