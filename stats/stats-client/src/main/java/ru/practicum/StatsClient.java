package ru.practicum;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class StatsClient {
    private final RestClient restClient;

    public StatsClient() {
        restClient = RestClient.builder().baseUrl("http://stats-server:9090").build();
    }

    public ResponseEntity<HitDto> post(@RequestBody HitDto hitDto) {
        return restClient.post()
                .uri(uriBuilder -> uriBuilder.path("/hit").build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitDto)
                .retrieve()
                .toEntity(HitDto.class);
    }

    public ResponseEntity<List<StatsDto>> get(String start, String end, List<String> uris, boolean unique) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/stats")
                            .queryParam("start", start)
                            .queryParam("end", end);
                    if (uris != null && !uris.isEmpty()) {
                        uriBuilder.queryParam("uris", String.join(", ", uris));
                    }
                    return uriBuilder.queryParam("unique", unique).build();
                })
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});
    }
}
