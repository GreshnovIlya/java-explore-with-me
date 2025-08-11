package ru.practicum.event.controller;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class PublicEventController {
    private final EventService service;

    @GetMapping
    private List<EventShortDto> getEventsByAdmin(@RequestParam(defaultValue = "") String text,
                                                 @RequestParam @Nullable List<Long> categories,
                                                 @RequestParam @Nullable Boolean paid,
                                                 @RequestParam @Nullable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String rangeStart,
                                                 @RequestParam @Nullable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String rangeEnd,
                                                 @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                 @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return service.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    private EventFullDto getEventByIdByAdmin(@PathVariable Long id) {
        return service.getEventById(id);
    }
}
