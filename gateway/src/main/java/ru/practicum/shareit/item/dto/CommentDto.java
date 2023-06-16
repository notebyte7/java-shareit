package ru.practicum.shareit.item.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Value
public class CommentDto {
    Integer id;
    @NotBlank(message = "Поле text не должно быть пустым")
    String text;
    Integer itemId;
    Integer authorId;
    LocalDateTime created;
}
