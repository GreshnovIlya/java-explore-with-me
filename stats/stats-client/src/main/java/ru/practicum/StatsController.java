package ru.practicum;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsClient statsClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> create(@RequestBody @Valid HitDto hitDto) {
        log.info("Запрос на сохранение информации о том, что на сервиса был отправлен запрос пользователем: {}",
                hitDto);
        return statsClient.post(hitDto);
    }

    @GetExchange("/stats")
    public ResponseEntity<Object> get(@RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                            LocalDateTime start,
                                      @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                            LocalDateTime end,
                                      @RequestParam @Nullable List<String> uris,
                                      @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Запрос на получение статистики по посещениям от {} до {} по uri в {} с уникальными {}",
                start, end, uris, unique);
        return statsClient.get(start, end, uris, unique);
    }
}
