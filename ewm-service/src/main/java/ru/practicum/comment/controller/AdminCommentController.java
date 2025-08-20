package ru.practicum.comment.controller;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/admin/events/comments")
@RestController
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getCommentByAdmin(@RequestParam @Nullable List<Long> authors,
                                              @RequestParam @Nullable List<Long> events,
                                              @RequestParam @Nullable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                              @RequestParam @Nullable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                              @RequestParam @Nullable Boolean visible,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getCommentByAdmin(authors, events, rangeStart, rangeEnd, visible, from, size);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateCommentByAdmin(@PathVariable Long commentId,
                                           @RequestParam(defaultValue = "false") Boolean visible) {
        return commentService.updateCommentByAdmin(commentId, visible);
    }
}