package ru.practicum.shareit.item.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class CommentDto {
    Integer id;
    String text;
    Integer itemId;
    Integer authorId;
    LocalDateTime created;
}
