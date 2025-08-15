package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.ParticipationRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    boolean existsByRequesterAndEvent(Long userId, Long eventId);

    List<ParticipationRequest> findByRequester(Long userId);

    List<ParticipationRequest> findByEvent(Long eventId);

    List<ParticipationRequest> findByEventAndStatus(Long eventId, String status);
}
