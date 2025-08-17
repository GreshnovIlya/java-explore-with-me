package ru.practicum.comment.model;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.PublicCommentDto;
import ru.practicum.user.model.UserMapper;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                              comment.getText(),
                              comment.getCreated(),
                              UserMapper.toUserDto(comment.getAuthor()),
                              comment.getEvent().getId(),
                              comment.getVisible());
    }

    public static PublicCommentDto toPublicCommentDto(Comment comment) {
        return new PublicCommentDto(comment.getId(),
                comment.getText(),
                comment.getCreated(),
                UserMapper.toUserDto(comment.getAuthor()));
    }
}