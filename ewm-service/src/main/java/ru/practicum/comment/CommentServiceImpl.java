package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.PublicCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.StateEvent;
import ru.practicum.exception.NewBadRequestException;
import ru.practicum.exception.NewConstraintViolationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentDto createComment(Long eventId, Long authorId, NewCommentDto newCommentDto) {
        if (newCommentDto.getText() == null) {
            throw new NewBadRequestException("Field: text. Error: must not be blank. Value: null");
        } else if (newCommentDto.getText().length() < 3 || newCommentDto.getText().length() > 7000) {
            throw new NewBadRequestException(String.format("Field: text. Error: it must be between 3 and 7000 " +
                    "characters long. Long: %s", newCommentDto.getText().length()));
        }
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Event with id=%s was not found", eventId)));
        if (event.getState() != StateEvent.PUBLISHED) {
            throw new NotFoundException("Event must be published");
        }
        User author = userRepository.findById(authorId).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%s was not found", authorId)));
        Comment com1 = new Comment(0L, newCommentDto.getText(), LocalDateTime.now(), author, event, true);
        Comment comment = commentRepository.save(com1);
        log.info("Пользователем с id = {} к событию с id = {} создан комментарий: {}", authorId, eventId, comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentByAuthor(Long authorId, Integer from, Integer size) {
        List<Comment> comments = commentRepository.findByAuthor(authorId, from, size);
        log.info("Пользователем с id = {} получены все его комментарии: {}", authorId, comments);
        return comments.stream().map(CommentMapper::toCommentDto).toList();
    }

    @Override
    public CommentDto updateCommentByAuthor(Long authorId, Long commentId, NewCommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException(String.format("Comment with id=%s was not found", commentId)));
        if (commentDto.getText() != null) {
            if (commentDto.getText().length() < 3 || commentDto.getText().length() > 7000) {
                throw new NewBadRequestException(String.format("Field: text. Error: it must be between 3 and 7000 " +
                        "characters long. Long: %s", commentDto.getText().length()));
            } else {
                comment.setText(commentDto.getText());
            }
        }
        if (!Objects.equals(comment.getAuthor().getId(), authorId)) {
            throw new NewConstraintViolationException(String.format("Field: authorId. Error: This user not id a " +
                    "create comment. Value: %s", authorId), "FORBIDDEN",
                    "For the requested operation the conditions are not met.");
        }
        comment = commentRepository.save(comment);
        log.info("Пользователем с id = {} обновил комментарий: {}", authorId, comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto updateCommentByAdmin(Long commentId, Boolean visible) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException(String.format("Comment with id=%s was not found", commentId)));
        comment.setVisible(visible);
        comment = commentRepository.save(comment);
        log.info("Админ установил комментарию с id = {}, значение видимости равное {}", commentId, visible);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentByAdmin(List<Long> authors, List<Long> events, LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd, Boolean visible, Integer from, Integer size) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusYears(1000);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now();
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new NewBadRequestException(String.format("Field: start. Error: rangeStart should be before " +
                    "rangeEnd. Value: %s", rangeStart));
        }
        List<Comment> comments;
        if (authors == null && events == null && visible != null) {
            comments = commentRepository.findByAdmin(rangeStart, rangeEnd, visible, from, size);
        } else if (authors == null && visible != null) {
            comments = commentRepository.findByAdminEvents(events, rangeStart, rangeEnd, visible, from, size);
        } else if (events == null && visible != null) {
            comments = commentRepository.findByAdminAuthors(authors, rangeStart, rangeEnd, visible, from, size);
        } else if (visible != null) {
            comments = commentRepository.findByAdminEventsAndAuthors(events, authors, rangeStart, rangeEnd, visible,
                                                                     from, size);
        } else if (authors == null && events == null) {
            comments = commentRepository.findByAdmin(rangeStart, rangeEnd, from, size);
        } else if (authors == null) {
            comments = commentRepository.findByAdminEvents(events, rangeStart, rangeEnd, from, size);
        } else if (events == null) {
            comments = commentRepository.findByAdminAuthors(authors, rangeStart, rangeEnd, from, size);
        } else {
            comments = commentRepository.findByAdminEventsAndAuthors(events, authors, rangeStart, rangeEnd, from, size);
        }
        return comments.stream().map(CommentMapper::toCommentDto).toList();
    }

    @Override
    public List<PublicCommentDto> getCommentByEvent(Long eventId, Integer from, Integer size) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Event with id=%s was not found", eventId)));
        if (event.getState() != StateEvent.PUBLISHED) {
            throw new NotFoundException("Event must be published");
        } else {
            List<Comment> comments = commentRepository.findByEventAndVisible(eventId, from, size);
            log.info("По событию с id = {} получены видимые комментарии: {}", eventId, comments);
            return comments.stream().map(CommentMapper::toPublicCommentDto).toList();
        }
    }
}