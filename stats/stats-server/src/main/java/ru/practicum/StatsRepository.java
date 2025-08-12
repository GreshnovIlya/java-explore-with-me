package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {
    @Query(value = """
            SELECT new ru.practicum.StatsDto(app, uri, COUNT(DISTINCT ip) AS hits)
            FROM Hit
            WHERE (timestamp BETWEEN ?1 AND ?2) AND ((?3 IS NULL) OR (uri IN ?3))
            GROUP BY app, uri
            ORDER BY hits DESC
            """)
    List<StatsDto> findByBetweenStartAndEndInUrisUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = """
            SELECT new ru.practicum.StatsDto(app, uri, COUNT(ip) AS hits)
            FROM Hit
            WHERE (timestamp BETWEEN ?1 AND ?2) AND ((?3 IS NULL) OR (uri IN ?3))
            GROUP BY app, uri
            ORDER BY hits DESC
            """)
    List<StatsDto> findByBetweenStartAndEndInUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}