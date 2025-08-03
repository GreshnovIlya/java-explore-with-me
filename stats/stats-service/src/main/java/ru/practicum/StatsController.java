package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto create(@RequestBody HitDto hitDto) {
        return statsService.create(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> get(@RequestParam Map<String, Object> parameters) {
        String s = parameters.get("uris").toString();
        List<String> uris = null;
        if (!s.equals("[]")) {
            uris = Arrays.stream(s.substring(1, s.length() - 1).split(", ")).toList();
        }
        return statsService.get(LocalDateTime.parse(parameters.get("start").toString()),
                LocalDateTime.parse(parameters.get("end").toString()),
                uris, parameters.get("unique").equals("true"));
    }

}
