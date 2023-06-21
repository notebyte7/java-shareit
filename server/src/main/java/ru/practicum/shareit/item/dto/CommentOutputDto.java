package ru.practicum.shareit.item.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class CommentOutputDto {
    Integer id;
    String text;
    Integer itemId;
    LocalDateTime created;
    String authorName;
}
