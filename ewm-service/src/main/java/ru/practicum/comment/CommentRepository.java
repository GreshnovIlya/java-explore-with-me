package ru.practicum.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = """
            SELECT *
            FROM comments
            WHERE (author_id = ?1)
            LIMIT ?3
            OFFSET ?2;
            """, nativeQuery = true)
    List<Comment> findByAuthor(Long author, Integer from, Integer size);

    @Query(value = """
            SELECT *
            FROM comments
            WHERE (event_id = ?1) AND (visible)
            LIMIT ?3
            OFFSET ?2;
            """, nativeQuery = true)
    List<Comment> findByEventAndVisible(Long event, Integer from, Integer size);

    @Query(value = """
            SELECT *
            FROM comments
            WHERE (created BETWEEN ?1 AND ?2)
                  AND (visible = ?3)
            LIMIT ?5
            OFFSET ?4;
            """, nativeQuery = true)
    List<Comment> findByAdmin(LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean visible, Integer from,
                              Integer size);

    @Query(value = """
            SELECT *
            FROM comments
            WHERE (event_id IN ?1)
                  AND (created BETWEEN ?2 AND ?3)
                  AND (visible = ?4)
            LIMIT ?6
            OFFSET ?5;
            """, nativeQuery = true)
    List<Comment> findByAdminEvents(List<Long> events, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    Boolean visible, Integer from, Integer size);

    @Query(value = """
            SELECT *
            FROM comments
            WHERE (author_id IN ?1)
                  AND (created BETWEEN ?2 AND ?3)
                  AND (visible = ?4)
            LIMIT ?6
            OFFSET ?5;
            """, nativeQuery = true)
    List<Comment> findByAdminAuthors(List<Long> authors, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    Boolean visible, Integer from, Integer size);

    @Query(value = """
            SELECT *
            FROM comments
            WHERE (event_id IN ?1)
                  AND (author_id IN ?1)
                  AND (created BETWEEN ?3 AND ?4)
                  AND (visible = ?5)
            LIMIT ?7
            OFFSET ?6;
            """, nativeQuery = true)
    List<Comment> findByAdminEventsAndAuthors(List<Long> events, List<Long> authors, LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd, Boolean visible, Integer from, Integer size);

    @Query(value = """
            SELECT *
            FROM comments
            WHERE (created BETWEEN ?1 AND ?2)
            LIMIT ?4
            OFFSET ?3;
            """, nativeQuery = true)
    List<Comment> findByAdmin(LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    @Query(value = """
            SELECT *
            FROM comments
            WHERE (event_id IN ?1)
                  AND (created BETWEEN ?2 AND ?3)
            LIMIT ?5
            OFFSET ?4;
            """, nativeQuery = true)
    List<Comment> findByAdminEvents(List<Long> events, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    Integer from, Integer size);

    @Query(value = """
            SELECT *
            FROM comments
            WHERE (author_id IN ?1)
                  AND (created BETWEEN ?2 AND ?3)
            LIMIT ?5
            OFFSET ?4;
            """, nativeQuery = true)
    List<Comment> findByAdminAuthors(List<Long> authors, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    Integer from, Integer size);

    @Query(value = """
            SELECT *
            FROM comments
            WHERE (event_id IN ?1)
                  AND (author_id IN ?1)
                  AND (created BETWEEN ?3 AND ?4)
            LIMIT ?6
            OFFSET ?5;
            """, nativeQuery = true)
    List<Comment> findByAdminEventsAndAuthors(List<Long> events, List<Long> authors, LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd, Integer from, Integer size);

}
