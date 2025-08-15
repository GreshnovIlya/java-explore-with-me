package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.*;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@AllArgsConstructor
public class PrivateEventController {
    private final EventService service;

    @GetMapping
    private List<EventShortDto> getEventsByInitiator(@PathVariable Long userId,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        return service.getEventsByInitiator(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private EventFullDto createEvent(@PathVariable Long userId, @RequestBody NewEventDto newEventDto) {
        return service.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    private EventFullDto getEventByInitiatorAndId(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.getEventByInitiatorAndId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    private EventFullDto updateEventByInitiatorAndId(@PathVariable Long userId, @PathVariable Long eventId,
                                                     @RequestBody UpdateEventUserRequest updateEventDto) {
        return service.updateEventByInitiatorAndId(userId, eventId, updateEventDto);
    }

    @GetMapping("/{eventId}/requests")
    List<ParticipationRequestDto> getRequestByUserAndEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.getRequestsByUserAndEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    EventRequestStatusUpdateResult updateStatusRequest(@PathVariable Long userId, @PathVariable Long eventId,
                                                     @RequestBody EventRequestStatusUpdateRequest requests) {
        return service.updateStatusRequest(userId, eventId, requests);
    }
}