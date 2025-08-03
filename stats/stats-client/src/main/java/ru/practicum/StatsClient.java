package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient {
    protected final RestTemplate rest;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String url, RestTemplateBuilder builder) {
        rest = builder.uriTemplateHandler(new DefaultUriBuilderFactory(url))
                      .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                      .build();
    }

    public ResponseEntity<HitDto> post(HitDto hitDto) {
        return rest.postForEntity("hit", hitDto, HitDto.class);
    }

    public ResponseEntity<Object[]> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", String.valueOf(start),
                "end", String.valueOf(end),
                "uris", uris,
                "unique", unique
        );
        return rest.getForEntity("stats?start={start}&end={end}&uris={uris}&unique={unique}", Object[].class,
                parameters);
    }
}
