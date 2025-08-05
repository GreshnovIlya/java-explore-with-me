package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsClient {

    private final RestClient restClient;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String url) {
        restClient = RestClient.builder().baseUrl(url).build();
    }

    public ResponseEntity<Object> post(HitDto hitDto) {
        return restClient.post()
                .uri(uriBuilder -> uriBuilder.path("/hit").build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitDto)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/stats")
                            .queryParam("start", URLEncoder.encode(start.toString(), StandardCharsets.UTF_8))
                            .queryParam("end", URLEncoder.encode(end.toString(), StandardCharsets.UTF_8));

                    if (uris != null && !uris.isEmpty()) {
                        uriBuilder.queryParam("uris", String.join(", ", uris));
                    }
                    return uriBuilder.queryParam("unique", unique).build();
                })
                .retrieve()
                .toEntity(Object.class);
    }
}
