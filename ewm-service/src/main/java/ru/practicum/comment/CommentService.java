package ru.practicum.comment;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.PublicCommentDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    CommentDto createComment(Long eventId, Long authorId, NewCommentDto newCommentDto);

    List<CommentDto> getCommentByAuthor(Long authorId, Integer from, Integer size);

    CommentDto updateCommentByAuthor(Long authorId, Long commentId, NewCommentDto commentDto);

    CommentDto updateCommentByAdmin(Long commentId, Boolean visible);

    List<CommentDto> getCommentByAdmin(List<Long> authors, List<Long> events, LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd, Boolean visible, Integer from, Integer size);

    List<PublicCommentDto> getCommentByEvent(Long eventId, Integer from, Integer size);
}