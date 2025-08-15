package ru.practicum.compilation.dto;

import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    private List<Long> events = new ArrayList<>();
    private Boolean pinned = false;
    private String title;
}