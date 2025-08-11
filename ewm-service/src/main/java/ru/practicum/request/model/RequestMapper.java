package ru.practicum.request.model;

import ru.practicum.request.dto.ParticipationRequestDto;

public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest requestDto) {
        return new ParticipationRequestDto(requestDto.getId(),
                                           requestDto.getCreated(),
                                           requestDto.getRequester(),
                                           requestDto.getEvent(),
                                           requestDto.getStatus());
    }
}
