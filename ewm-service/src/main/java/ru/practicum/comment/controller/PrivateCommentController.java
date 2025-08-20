package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

@RequestMapping("/users/{authorId}/events")
@RestController
@RequiredArgsConstructor
@Slf4j
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long authorId,
                                    @PathVariable Long eventId,
                                    @RequestBody NewCommentDto commentDto) {
        return commentService.createComment(eventId, authorId, commentDto);
    }

    @GetMapping("/comments")
    public List<CommentDto> getCommentByAuthor(@PathVariable Long authorId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getCommentByAuthor(authorId, from, size);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentDto updateCommentByAuthor(@PathVariable Long authorId,
                                            @PathVariable Long commentId,
                                            @RequestBody NewCommentDto commentDto) {
        return commentService.updateCommentByAuthor(authorId, commentId, commentDto);
    }
}