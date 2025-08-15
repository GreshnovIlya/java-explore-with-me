package ru.practicum.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.StateEvent;
import ru.practicum.exception.NewConstraintViolationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestMapper;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%s was not found", userId)));
        List<ParticipationRequest> requests = requestRepository.findByRequester(userId);
        log.info("Получены запросы пользователя: {}", requests);
        return requests.stream().map(RequestMapper::toParticipationRequestDto).toList();
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%s was not found", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Event with id=%s was not found", eventId)));
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NewConstraintViolationException("The initiator cannot submit a request for his event.",
                    "CONFLICT", "For the requested operation the conditions are not met.");
        }
        if (requestRepository.existsByRequesterAndEvent(userId, eventId)) {
            throw new NewConstraintViolationException("A user cannot submit a request twice", "CONFLICT",
                    "Integrity constraint has been violated.");
        }
        if (event.getState() != StateEvent.PUBLISHED) {
            throw new NewConstraintViolationException("The event request cannot be created because it is not in " +
                    "the PUBLISHED state.", "CONFLICT", "For the requested operation the conditions are not met.");
        }
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new NewConstraintViolationException("The event request cannot be created because all the seats are " +
                    "occupied", "CONFLICT", "For the requested operation the conditions are not met.");
        }
        ParticipationRequest request = new ParticipationRequest(0L, LocalDateTime.now(), userId, eventId,
                "PENDING");
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus("CONFIRMED");
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        request = requestRepository.save(request);
        log.info("Создан запрос: {}", request);
        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Request with id=%s was not found", requestId)));
        if (!Objects.equals(request.getRequester(), userId)) {
            throw new NewConstraintViolationException("Only requester can cancel request.",
                    "CONFLICT", "For the requested operation the conditions are not met.");
        }
        if (request.getStatus().equals("PENDING")) {
            request.setStatus("CANCELED");
            request = requestRepository.save(request);
            log.info("Запрос отменен: {}", request);
        } else {
            throw new NewConstraintViolationException("Request cannot be canceled", "CONFLICT",
                    "For the requested operation the conditions are not met.");
        }
        return RequestMapper.toParticipationRequestDto(request);
    }
}
