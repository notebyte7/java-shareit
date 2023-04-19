package ru.practicum.shareit.item.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Value
public class CommentOutputDto {
    Integer id;
    @NotBlank(message = "Поле text не должно быть пустым")
    String text;
    Integer itemId;
    LocalDateTime created;
    String authorName;
}
