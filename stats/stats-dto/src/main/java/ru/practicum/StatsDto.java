package ru.practicum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StatsDto {
    @NotBlank(message = "Название сервиса не может быть пустым")
    private String app;

    @NotBlank(message = "URI сервиса не может быть пустым")
    private String uri;

    @NotBlank(message = "Количество просмотров не может быть пустым")
    private long hits;
}
