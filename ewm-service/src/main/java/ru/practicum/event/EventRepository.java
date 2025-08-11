package ru.practicum.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = """
            SELECT *
            FROM events
            WHERE (initiator = ?1)
            LIMIT ?3
            OFFSET ?2;
            """, nativeQuery = true)
    List<Event> findByUserFromAndSize(Long userId, Long from, Long size);

    @Query(value = """
            SELECT *
            FROM events
            WHERE (initiator IN ?1) AND (state IN ?2) AND (category IN ?3)
            AND (event_date BETWEEN ?4 AND ?5)
            LIMIT ?7
            OFFSET ?6;
            """, nativeQuery = true)
    List<Event> findByAdminUsersAndStatesAndCategories(List<Long> users, List<String> states, List<Long> categories,
                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from, Long size);

    @Query(value = """
            SELECT *
            FROM events
            WHERE (initiator IN ?1) AND (state IN ?2)
            AND (event_date BETWEEN ?3 AND ?4)
            LIMIT ?6
            OFFSET ?5;
            """, nativeQuery = true)
    List<Event> findByAdminUsersAndStates(List<Long> users, List<String> states,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from, Long size);

    @Query(value = """
            SELECT *
            FROM events
            WHERE (initiator IN ?1) AND (category IN ?2)
            AND (event_date BETWEEN ?3 AND ?4)
            LIMIT ?6
            OFFSET ?5;
            """, nativeQuery = true)
    List<Event> findByAdminUsersAndCategories(List<Long> users, List<Long> categories,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from, Long size);

    @Query(value = """
            SELECT *
            FROM events
            WHERE (state IN ?1) AND (category IN ?2)
            AND (event_date BETWEEN ?3 AND ?4)
            LIMIT ?6
            OFFSET ?5;
            """, nativeQuery = true)
    List<Event> findByAdminStatesAndCategories(List<String> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from, Long size);

    @Query(value = """
            SELECT *
            FROM events
            WHERE (initiator IN ?1)
            AND (event_date BETWEEN ?2 AND ?3)
            LIMIT ?5
            OFFSET ?4;
            """, nativeQuery = true)
    List<Event> findByAdminUsers(List<Long> users, LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from,
                                 Long size);

    @Query(value = """
            SELECT *
            FROM events
            WHERE (category IN ?2)
            AND (event_date BETWEEN ?2 AND ?3)
            LIMIT ?5
            OFFSET ?4;
            """, nativeQuery = true)
    List<Event> findByAdminCategories(List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                      Long from, Long size);

    @Query(value = """
            SELECT *
            FROM events
            WHERE (state IN ?1)
            AND (event_date BETWEEN ?2 AND ?3)
            LIMIT ?5
            OFFSET ?4;
            """, nativeQuery = true)
    List<Event> findByAdminStates(List<String> states, LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from,
                                  Long size);

    @Query(value = """
            SELECT *
            FROM events
            WHERE (event_date BETWEEN ?1 AND ?2)
            LIMIT ?4
            OFFSET ?3;
            """, nativeQuery = true)
    List<Event> findByAdmin(LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from, Long size);

    @Query(value = """
            SELECT *
            FROM events
            WHERE (category = ?1)
            LIMIT 1;
            """, nativeQuery = true)
    List<Event> findByCategory(Long category);

    @Query(value = """
            SELECT *
            FROM events
            WHERE ((LOWER(annotation) LIKE '%' || LOWER(?1) || '%') OR (LOWER(description) LIKE '%' || LOWER(?1) || '%'))
                    AND (category IN ?2)
                    AND (paid = ?3 OR ?3 IS NULL)
                    AND (event_date BETWEEN ?4 AND ?5)
                    AND (state = 'PUBLISHED')
            ORDER BY ?6
            LIMIT ?8
            OFFSET ?7;
            """, nativeQuery = true)
    List<Event> findEventByCategoriesAndPaid(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                          LocalDateTime rangeEnd, String sort, Integer from, Integer size);

    @Query(value = """
            SELECT *
            FROM events
            WHERE ((LOWER(annotation) LIKE '%' || LOWER(?1) || '%') OR (LOWER(description) LIKE '%' || LOWER(?1) || '%'))
                    AND (paid = ?2 OR ?2 IS NULL)
                    AND (event_date BETWEEN ?3 AND ?4)
                    AND (state = 'PUBLISHED')
            ORDER BY ?5
            LIMIT ?7
            OFFSET ?6;
            """, nativeQuery = true)
    List<Event> findEventByPaid(String text, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, String sort,
                          Integer from, Integer size);

    @Query(value = """
            SELECT *
            FROM events
            WHERE ((LOWER(annotation) LIKE '%' || LOWER(?1) || '%') OR (LOWER(description) LIKE '%' || LOWER(?1) || '%'))
                AND (category IN ?2)
                AND (paid = ?3 OR ?3 IS NULL)
                AND (event_date BETWEEN ?4 AND ?5)
                AND (confirmed_requests < participant_limit)
                AND (state = 'PUBLISHED')
            ORDER BY ?6
            LIMIT ?8
            OFFSET ?7;
            """, nativeQuery = true)
    List<Event> findEventWhereOnlyAvailableByCategoriesAndPaid(String text, List<Long> categories, Boolean paid,
                                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, String sort,
                                                        Integer from, Integer size);

    @Query(value = """
            SELECT *
            FROM events
            WHERE ((LOWER(annotation) LIKE '%' || LOWER(?1) || '%') OR (LOWER(description) LIKE '%' || LOWER(?1) || '%'))
                    AND (paid = ?2 OR ?2 IS NULL)
                    AND (event_date BETWEEN ?3 AND ?4)
                    AND (confirmed_requests < participant_limit)
                    AND (state = 'PUBLISHED')
            ORDER BY ?5
            LIMIT ?7
            OFFSET ?6;
            """, nativeQuery = true)
    List<Event> findEventWhereOnlyAvailableByPaid(String text, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                            String sort, Integer from, Integer size);
}