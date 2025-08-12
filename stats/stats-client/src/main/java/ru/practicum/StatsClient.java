package ru.practicum;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

@Controller
public class StatsClient {

    private final RestClient restClient;

    public StatsClient() {
        restClient = RestClient.builder().baseUrl("http://localhost:9090").build();
    }

    @PostMapping("/hit")
    public ResponseEntity<HitDto> post(@RequestBody @Valid HitDto hitDto) {
        return restClient.post()
                .uri(uriBuilder -> uriBuilder.path("/hit").build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitDto)
                .retrieve()
                .toEntity(HitDto.class);
    }

    @GetExchange("/stats")
    public ResponseEntity<List<StatsDto>> get(@RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                  String start,
                                              @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                  String end,
                                              @RequestParam @Nullable List<String> uris,
                                              @RequestParam(defaultValue = "false") boolean unique) {
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
