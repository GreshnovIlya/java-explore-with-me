package ru.practicum.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.StatsController;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.*;
import ru.practicum.exception.NewBadRequestException;
import ru.practicum.exception.NewConstraintViolationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.RequestRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestMapper;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final StatsController statsController;


    @Override
    public List<EventShortDto> getEventsByInitiator(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%s was not found", userId)));

        return eventRepository.findByUserFromAndSize(userId, Long.valueOf(from), Long.valueOf(size))
                .stream().map(EventMapper::toEventShortDto).toList();
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getAnnotation() == null) {
            throw new NewBadRequestException("Field: annotation. Error: must not be blank. Value: null");
        } else if (newEventDto.getAnnotation().length() > 2000 || newEventDto.getAnnotation().length() < 20
                || newEventDto.getAnnotation().isBlank()) {
            throw new NewBadRequestException(String.format("Field: annotation. Error: it must be between 20 and 2000 " +
                    "characters long. Long: %s", newEventDto.getAnnotation().length()));
        }
        if (newEventDto.getCategory() == null) {
            throw new NewBadRequestException("Field: category. Error: must not be blank. Value: null");
        }
        if (newEventDto.getDescription() == null) {
            throw new NewBadRequestException("Field: description. Error: must not be blank. Value: null");
        } else if (newEventDto.getDescription().length() > 7000 || newEventDto.getDescription().length() < 20
                || newEventDto.getDescription().isBlank()) {
            throw new NewBadRequestException(String.format("Field: description. Error: it must be between 20 and 7000 " +
                    "characters long. Long: %s", newEventDto.getDescription().length()));
        }
        if (newEventDto.getEventDate() == null) {
            throw new NewBadRequestException("Field: eventDate. Error: must not be blank. Value: null");
        }
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new NewBadRequestException(String.format("Field: eventDate. Error: должно " +
                    "содержать дату, большую на 2 часа по сравнению с текущей. Value: %s", newEventDto.getEventDate()));
        }
        if (newEventDto.getLocation() == null || newEventDto.getLocation().getLat() == null ||
                newEventDto.getLocation().getLon() == null) {
            throw new NewBadRequestException("Field: location. Error: must not be blank. Value: null");
        }
        if (newEventDto.getPaid() == null) {
            throw new NewBadRequestException("Field: paid. Error: must not be blank. Value: null");
        }
        if (newEventDto.getParticipantLimit() == null || newEventDto.getParticipantLimit() < 0) {
            throw new NewBadRequestException(String.format("Field: participantLimit. Error: must be positive. " +
                    "Value: %s", newEventDto.getParticipantLimit()));
        }
        if (newEventDto.getTitle() == null) {
            throw new NewBadRequestException("Field: title. Error: must not be blank. Value: null");
        } else if (newEventDto.getTitle().length() > 120 || newEventDto.getTitle().length() < 3
                || newEventDto.getTitle().isBlank()) {
            throw new NewBadRequestException(String.format("Field: title. Error: it must be between 3 and 120 " +
                    "characters long. Long: %s", newEventDto.getTitle().length()));
        }
        Event event;
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
                () -> new NotFoundException(String.format("Category with id=%s was not found", newEventDto.getCategory())));
        User initiator = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%s was not found", userId)));
        try {
             Location location = locationRepository.save(LocationMapper.toLocation(null, newEventDto.getLocation()));
             event = eventRepository.save(EventMapper.toEvent(0L, newEventDto, location, category, 0L,
                        LocalDateTime.now(), initiator, null, newEventDto.getRequestModeration(),
                        StateEvent.PENDING, 0L));
        } catch (RuntimeException e) {
            throw new NewConstraintViolationException(e.getMessage(), "CONFLICT", "Integrity constraint has been violated.");
        }
        log.info("Создано новое событие: {}", event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto getEventByInitiatorAndId(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%s was not found", userId)));
        return EventMapper.toEventFullDto(eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Event with id=%s was not found", eventId))));
    }

    @Override
    public EventFullDto updateEventByInitiatorAndId(Long userId, Long eventId, UpdateEventUserRequest updateEventDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Event with id=%s was not found", eventId)));
        if (event.getState() == StateEvent.PUBLISHED) {
            throw new NewConstraintViolationException("Cannot publish the event because it's not in the right state: " +
                    "PUBLISHED", "FORBIDDEN", "For the requested operation the conditions are not met.");
        }
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NewConstraintViolationException(String.format("Field: userId. Error: This user not id a " +
                    "initiator. Value: %s", userId), "FORBIDDEN",
                    "For the requested operation the conditions are not met.");
        }
        if (updateEventDto.getAnnotation() != null) {
            if (updateEventDto.getAnnotation().length() > 2000 || updateEventDto.getAnnotation().length() < 20
                    || updateEventDto.getAnnotation().isBlank()) {
                throw new NewBadRequestException(String.format("Field: annotation. Error: it must be between 20 and 2000 " +
                        "characters long. Long: %s", updateEventDto.getAnnotation().length()));
            }
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventDto.getCategory()).orElseThrow(
                    () -> new NotFoundException(String.format("Category with id=%s was not found",
                            updateEventDto.getCategory()))));
        }
        if (updateEventDto.getDescription() != null) {
            if (updateEventDto.getDescription().length() > 7000 || updateEventDto.getDescription().length() < 20
                    || updateEventDto.getDescription().isBlank()) {
                throw new NewBadRequestException(String.format("Field: description. Error: it must be between 20 and 7000 " +
                        "characters long. Long: %s", updateEventDto.getDescription().length()));
            }
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getLocation() != null && updateEventDto.getLocation().getLat() != null
                && updateEventDto.getLocation().getLon() != null) {
            Location location = locationRepository.save(LocationMapper.toLocation(0L, updateEventDto.getLocation()));
            event.setLocation(location);
        }
        if (updateEventDto.getEventDate() != null) {
            if (updateEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new NewBadRequestException(String.format("Field: eventDate. Error: должно " +
                        "содержать дату, большую на 2 часа по сравнению с текущей. Value: %s", updateEventDto.getEventDate()));
            } else {
                event.setEventDate(updateEventDto.getEventDate());
            }
        }
        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != null) {
            if (updateEventDto.getParticipantLimit() < 0) {
                throw new NewBadRequestException(String.format("Field: participantLimit. Error: must be positive. " +
                        "Value: %s", updateEventDto.getParticipantLimit()));
            }
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getTitle() != null) {
            if (updateEventDto.getTitle().length() > 120 || updateEventDto.getTitle().length() < 3
                    || updateEventDto.getTitle().isBlank()) {
                throw new NewBadRequestException(String.format("Field: title. Error: it must be between 3 and 120 " +
                        "characters long. Long: %s", updateEventDto.getTitle().length()));
            }
            event.setTitle(updateEventDto.getTitle());
        }
        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }
        if (updateEventDto.getStateAction() != null && updateEventDto.getStateAction().equals("SEND_TO_REVIEW")) {
            event.setState(StateEvent.PENDING);
        } else if (updateEventDto.getStateAction() != null && updateEventDto.getStateAction().equals("CANCEL_REVIEW")) {
            event.setState(StateEvent.CANCELED);
        } else if (updateEventDto.getStateAction() != null) {
            throw new NewConstraintViolationException(String.format("Field: state. Error: State can be " +
                    "SEND_TO_REVIEW or CANCEL_REVIEW. Value: %s", updateEventDto.getStateAction()), "FORBIDDEN",
                    "For the requested operation the conditions are not met.");
        }
        try {
            event = eventRepository.save(event);
        } catch (RuntimeException e) {
            throw new NewConstraintViolationException(e.getMessage(), "CONFLICT",
                    "Integrity constraint has been violated.");
        }
        log.info("Инициатор обновил событие: {}", event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUserAndEvent(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Event with id=%s was not found", eventId)));
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NewConstraintViolationException(String.format("Field: userId. Error: This user not id a " +
                    "initiator. Value: %s", userId), "FORBIDDEN",
                    "For the requested operation the conditions are not met.");
        }
        List<ParticipationRequest> requests = requestRepository.findByEvent(eventId);
        log.info("Получены запросы пользователей: {}", requests);
        return requests.stream().map(RequestMapper::toParticipationRequestDto).toList();
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId,
                                                                    EventRequestStatusUpdateRequest requests) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Event with id=%s was not found", eventId)));
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new NewBadRequestException("No confirmation of applications is required");
        }
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NewConstraintViolationException(String.format("Field: userId. Error: This user not id a " +
                    "initiator. Value: %s", userId), "FORBIDDEN",
                    "For the requested operation the conditions are not met.");
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new NewConstraintViolationException("The event request cannot be confirmed because all the " +
                    "seats are occupied", "CONFLICT", "For the requested operation the conditions are not met.");
        }
        List<ParticipationRequestDto> confirmedRequest = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequest = new ArrayList<>();
        if (requests.getStatus().equals("CONFIRMED") || requests.getStatus().equals("REJECTED")) {
            for (Long requestId : requests.getRequestIds()) {
                ParticipationRequest request = requestRepository.findById(requestId).orElseThrow(
                        () -> new NewBadRequestException(String.format("Request with id=%s was not found", requestId)));
                if (!request.getStatus().equals("PENDING")) {
                    throw new NewConstraintViolationException(String.format("Field: status. Error: Status request " +
                            "should be PENDING. Value: %s", userId), "FORBIDDEN",
                            "For the requested operation the conditions are not met.");
                }
                if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                    if (requests.getStatus().equals("CONFIRMED")) {
                        request.setStatus("CONFIRMED");
                        request = requestRepository.save(request);
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                        eventRepository.save(event);
                        confirmedRequest.add(RequestMapper.toParticipationRequestDto(request));
                    } else {
                        request.setStatus("REJECTED");
                        request = requestRepository.save(request);
                        rejectedRequest.add(RequestMapper.toParticipationRequestDto(request));
                    }
                }
            }
            if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
                List<ParticipationRequest> pendingRequests = requestRepository.findByEventAndStatus(eventId,
                        "PENDING");
                for (ParticipationRequest request : pendingRequests) {
                    request.setStatus("REJECTED");
                    request = requestRepository.save(request);
                    rejectedRequest.add(RequestMapper.toParticipationRequestDto(request));
                }
            }
            EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(confirmedRequest, rejectedRequest);
            log.info("Изменены статусы запросов: {}", result);
            return result;
        } else {
            throw new NewConstraintViolationException(String.format("Field: status. Error: Status should be " +
                    "CONFIRMED or REJECTED. Value: %s", userId), "FORBIDDEN",
                    "For the requested operation the conditions are not met.");
        }
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                        String rangeStart, String rangeEnd, Integer from, Integer size) {
        LocalDateTime start;
        LocalDateTime end;
        if (rangeStart == null) {
            start = LocalDateTime.now();
        } else {
            start = toLocalDateTime(rangeStart);
        }
        if (rangeEnd == null) {
            end = LocalDateTime.now().plusYears(1000);
        } else {
            end = toLocalDateTime(rangeEnd);
        }
        if (start.isAfter(end)) {
            throw new NewBadRequestException(String.format("Field: start. Error: rangeStart should be before " +
                    "rangeEnd. Value: %s", start));
        }
        List<Event> events;
        if (users == null && states == null && categories == null) {
            events = eventRepository.findByAdmin(start, end, Long.valueOf(from), Long.valueOf(size));
        } else if (users != null && states == null && categories == null) {
            events = eventRepository.findByAdminUsers(users, start, end, Long.valueOf(from), Long.valueOf(size));
        } else if (users == null && states != null && categories == null) {
            events = eventRepository.findByAdminStates(states, start, end, Long.valueOf(from), Long.valueOf(size));
        } else if (users == null && states == null) {
            events = eventRepository.findByAdminCategories(categories, start, end, Long.valueOf(from),
                    Long.valueOf(size));
        } else if (users != null && states != null && categories == null) {
            events = eventRepository.findByAdminUsersAndStates(users, states, start, end, Long.valueOf(from),
                    Long.valueOf(size));
        } else if (users == null) {
            events = eventRepository.findByAdminStatesAndCategories(states, categories, start, end, Long.valueOf(from),
                    Long.valueOf(size));
        } else if (states == null) {
            events = eventRepository.findByAdminUsersAndCategories(users, categories, start, end, Long.valueOf(from),
                    Long.valueOf(size));
        } else {
            events = eventRepository.findByAdminUsersAndStatesAndCategories(users, states, categories, start, end,
                    Long.valueOf(from), Long.valueOf(size));
        }
        return events.stream().map(EventMapper::toEventFullDto).toList();
    }

    @Override
    public EventFullDto updateEventByInitiatorAndIdAdmin(Long eventId,
                                                         UpdateEventAdminRequest updateEventDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Event with id=%s was not found", eventId)));
        if (event.getState() != StateEvent.PENDING) {
            throw new NewConstraintViolationException(String.format("Cannot publish the event because it's not in " +
                    "the right state: %s", event.getState()), "FORBIDDEN", "For the requested operation the " +
                    "conditions are not met.");
        }
        if (updateEventDto.getAnnotation() != null) {
            if (updateEventDto.getAnnotation().length() > 2000 || updateEventDto.getAnnotation().length() < 20
                    || updateEventDto.getAnnotation().isBlank()) {
                throw new NewBadRequestException(String.format("Field: annotation. Error: it must be between 20 and 2000 " +
                        "characters long. Long: %s", updateEventDto.getAnnotation().length()));
            }
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventDto.getCategory()).orElseThrow(
                    () -> new NotFoundException(String.format("Category with id=%s was not found",
                            updateEventDto.getCategory()))));
        }
        if (updateEventDto.getDescription() != null) {
            if (updateEventDto.getDescription().length() > 7000 || updateEventDto.getDescription().length() < 20
                    || updateEventDto.getDescription().isBlank()) {
                throw new NewBadRequestException(String.format("Field: description. Error: it must be between 20 and 7000 " +
                        "characters long. Long: %s", updateEventDto.getDescription().length()));
            }
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getLocation() != null && updateEventDto.getLocation().getLat() != null
                && updateEventDto.getLocation().getLon() != null) {
            Location location = locationRepository.save(LocationMapper.toLocation(0L, updateEventDto.getLocation()));
            event.setLocation(location);
        }
        if (updateEventDto.getEventDate() != null) {
            if (updateEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new NewBadRequestException(String.format("Field: eventDate. Error: должно " +
                        "содержать дату, большую на 2 часа по сравнению с текущей. Value: %s", updateEventDto.getEventDate()));
            } else {
                event.setEventDate(updateEventDto.getEventDate());
            }
        }
        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != null) {
            if (updateEventDto.getParticipantLimit() < 0) {
                throw new NewBadRequestException(String.format("Field: participantLimit. Error: must be positive. " +
                        "Value: %s", updateEventDto.getParticipantLimit()));
            }
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getTitle() != null) {
            if (updateEventDto.getTitle().length() > 120 || updateEventDto.getTitle().length() < 3
                    || updateEventDto.getTitle().isBlank()) {
                throw new NewBadRequestException(String.format("Field: title. Error: it must be between 3 and 120 " +
                        "characters long. Long: %s", updateEventDto.getTitle().length()));
            }
            event.setTitle(updateEventDto.getTitle());
        }
        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }
        if (updateEventDto.getStateAction() != null && updateEventDto.getStateAction().equals("PUBLISH_EVENT")) {
            event.setState(StateEvent.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else if (updateEventDto.getStateAction() != null && updateEventDto.getStateAction().equals("REJECT_EVENT")) {
            event.setState(StateEvent.CANCELED);
        } else if (updateEventDto.getStateAction() != null) {
            throw new NewConstraintViolationException(String.format("Field: state. Error: State can be " +
                    "PUBLISH_EVENT or REJECT_EVENT. Value: %s", updateEventDto.getStateAction()), "FORBIDDEN",
                    "For the requested operation the conditions are not met.");
        }
        try {
            event = eventRepository.save(event);
        } catch (RuntimeException e) {
            throw new NewConstraintViolationException(e.getMessage(), "CONFLICT",
                    "Integrity constraint has been violated.");
        }
        log.info("Админ обновил событие: {}", event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                         String rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                         Integer size) {
        if (sort.equals("EVENT_DATE") || sort.equals("VIEWS")) {
            LocalDateTime start;
            LocalDateTime end;
            if (rangeStart == null) {
                start = toLocalDateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                start = toLocalDateTime(rangeStart);
            }
            if (rangeEnd == null) {
                end = toLocalDateTime(LocalDateTime.now().plusYears(1000)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                end = toLocalDateTime(rangeEnd);
            }
            if (start.isAfter(end)) {
                throw new NewBadRequestException(String.format("Field: start. Error: rangeStart should be before " +
                        "rangeEnd. Value: %s", start));
            }
            List<Event> events;
            try {
                statsController.create(new HitDto(null, "ewm-service", "/events", "127.0.0.1", LocalDateTime.now()));
            } catch (Exception ignored) {
            }
            if (onlyAvailable && categories == null) {
                events = eventRepository.findEventWhereOnlyAvailableByPaid(text, paid, start, end, sort, from, size);
            } else if (onlyAvailable) {
                events = eventRepository.findEventWhereOnlyAvailableByCategoriesAndPaid(text, categories, paid, start, end, sort, from, size);
            } else if (categories == null) {
                events = eventRepository.findEventByPaid(text, paid, start, end, sort, from, size);
            } else {
                events = eventRepository.findEventByCategoriesAndPaid(text, categories, paid, start, end,
                        sort, from, size);
            }
            return events.stream().map(EventMapper::toEventShortDto).toList();
        } else {
            throw new NewBadRequestException(String.format("Field: sort. Error: must not be EVENT_DATE or VIEWS. " +
                    "Value: %s", sort));
        }
    }

    @Override
    public EventFullDto getEventById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Event with id=%s was not found", id)));
        if (event.getState() != StateEvent.PUBLISHED) {
            throw new NotFoundException("Event must be published");
        }
        try {
            statsController.create(new HitDto(null, "ewm-service", "/events/" + id, "127.0.0.1", LocalDateTime.now()));
            event.setViews(statsController.get(
                    LocalDateTime.now().minusYears(100).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    new ArrayList<>(List.of("/events/" + id)), true).getBody().stream().count());
        } catch (Exception e) {
            event.setViews(1L);
        }
        event = eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }


    private LocalDateTime toLocalDateTime(String date) {
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            throw new NewBadRequestException(String.format("Failed to convert value of type java.lang.String to " +
                    "required type java.time.LocalDateTime; nested exception is " +
                    "java.time.format.DateTimeParseException: For input string: %s", date));
        }
    }
}