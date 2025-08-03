package ru.practicum;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HitDto {
    private Long id;

    @NotNull(message = "Идентификатор сервиса для которого записывается информация не может быть пустым")
    private String app;

    @NotNull(message = "URI для которого был осуществлен запрос не может быть пустым")
    private String uri;

    @NotNull(message = "IP-адрес пользователя, осуществившего запрос не может быть пустым")
    private String ip;

    @NotNull(message = "Дата и время, когда был совершен запрос к эндпоинту (в формате \"yyyy-MM-dd HH:mm:ss\") " +
            "не может быть пустым")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
