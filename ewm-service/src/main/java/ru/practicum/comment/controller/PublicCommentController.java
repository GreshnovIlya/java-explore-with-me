package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.PublicCommentDto;

import java.util.List;

@RequestMapping("/events/{eventId}/comments")
@RestController
@RequiredArgsConstructor
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<PublicCommentDto> getCommentByEvent(@PathVariable Long eventId,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getCommentByEvent(eventId, from, size);
    }
}