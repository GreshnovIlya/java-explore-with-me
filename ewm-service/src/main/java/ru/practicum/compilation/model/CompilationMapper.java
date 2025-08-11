package ru.practicum.compilation.model;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventMapper;

import java.util.Set;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCategoryDto, Set<Event> events) {
        return new Compilation(0L,
                               events,
                               newCategoryDto.getPinned(),
                               newCategoryDto.getTitle());
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(compilation.getId(),
                compilation.getEvents().stream().map(EventMapper::toEventShortDto).toList(),
                compilation.getPinned(),
                compilation.getTitle());
    }
}
