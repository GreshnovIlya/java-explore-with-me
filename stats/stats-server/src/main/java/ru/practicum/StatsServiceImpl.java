package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NewBadRequestException;
import ru.practicum.model.Hit;
import ru.practicum.model.HitMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public HitDto create(HitDto hitDto) {
        Hit newHit = statsRepository.save(HitMapper.toHit(hitDto));
        log.info("Сохранение информации о том, что на сервиса был отправлен запрос пользователем: {}", newHit);
        return HitMapper.toHitDto(newHit);
    }

    @Override
    public List<StatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new NewBadRequestException("Начало времени не может быть позже конца");
        }
        List<StatsDto> viewStatsDto;
        if (unique) {
            viewStatsDto = statsRepository.findByBetweenStartAndEndInUrisUnique(start, end, uris);
        } else {
            viewStatsDto = statsRepository.findByBetweenStartAndEndInUris(start, end, uris);
        }
        log.info("Получение статистики по посещениям : {}", viewStatsDto);
        return viewStatsDto;
    }
}
