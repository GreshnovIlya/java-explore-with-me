package ru.practicum.compilation.dto;

import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationDto {
    private List<Long> events;
    private Boolean pinned;
    private String title;
}