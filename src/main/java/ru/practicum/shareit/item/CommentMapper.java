package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentWithName;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItemId(),
                comment.getAuthor().getId(),
                comment.getCreated()
        );
    }

    public static CommentWithName toCommentWithName(Comment comment) {
        return new CommentWithName(
                comment.getId(),
                comment.getText(),
                comment.getItemId(),
                comment.getCreated(),
                comment.getAuthor().getName()

        );
    }

    public static Comment toComment(CommentDto commentDto, User user) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                commentDto.getItemId(),
                user,
                commentDto.getCreated()
        );
    }
}
