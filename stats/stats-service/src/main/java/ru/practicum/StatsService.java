package ru.practicum;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    HitDto create(HitDto hitDto);

    List<StatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
