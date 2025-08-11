package ru.practicum.compilation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NewBadRequestException;
import ru.practicum.exception.NewConstraintViolationException;
import ru.practicum.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Set<Event> events = new HashSet<>();
        for (Long eventId : newCompilationDto.getEvents()) {
            events.add(eventRepository.findById(eventId).orElseThrow(
                    () -> new NotFoundException(String.format("Event with id=%s was not found", eventId))));
        }
        if (newCompilationDto.getTitle() == null) {
            throw new NewBadRequestException("Field: title. Error: must not be blank. Value: null");
        } else if (newCompilationDto.getTitle().length() > 50 || newCompilationDto.getTitle().isBlank()) {
            throw new NewBadRequestException(String.format("Field: title. Error: it must be between 1 and 50 " +
                    "characters long. Long: %s", newCompilationDto.getTitle().length()));
        }
        if (compilationRepository.existsByTitle(newCompilationDto.getTitle())) {
            throw new NewConstraintViolationException("could not execute statement; SQL [n/a]; constraint " +
                    "[uq_compilation_title]; nested exception is org.hibernate.exception.ConstraintViolationException: " +
                    "could not execute statement", "CONFLICT", "Integrity constraint has been violated.");
        }
        Compilation compilation = compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, events));
        log.info("Админ создал новую подборку: {}", compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public void removeCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(String.format("Compilation with id=%s was not found", compId)));
        compilationRepository.delete(compilation);
        log.info("Админ удалил подборку: {}", compilation);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(String.format("Compilation with id=%s was not found", compId)));
        if (updateCompilationDto.getEvents() != null) {
            Set<Event> events = new HashSet<>();
            for (Long eventId : updateCompilationDto.getEvents()) {
                events.add(eventRepository.findById(eventId).orElseThrow(
                        () -> new NotFoundException(String.format("Event with id=%s was not found", eventId))));
            }
            compilation.setEvents(events);
        }
        if (updateCompilationDto.getPinned() != null) {
            compilation.setPinned(updateCompilationDto.getPinned());
        }
        if (updateCompilationDto.getTitle() != null) {
            if (updateCompilationDto.getTitle().length() > 50 || updateCompilationDto.getTitle().isBlank()) {
                throw new NewBadRequestException(String.format("Field: title. Error: it must be between 1 and 50 " +
                        "characters long. Long: %s", updateCompilationDto.getTitle().length()));
            }

            List<Compilation> compilations = compilationRepository.findByTitle(updateCompilationDto.getTitle());
            if (compilations.isEmpty() || (compilations.size() == 1 &&
                    compilations.getFirst().getTitle().equals(compilation.getTitle()))) {
                compilation.setTitle(updateCompilationDto.getTitle());
            } else {
                throw new NewConstraintViolationException("could not execute statement; SQL [n/a]; constraint " +
                        "[uq_compilation_title]; nested exception is org.hibernate.exception.ConstraintViolationException: " +
                        "could not execute statement", "CONFLICT", "Integrity constraint has been violated.");
            }
        }
        compilation = compilationRepository.save(compilation);
        log.info("Админ обновил подборку: {}", compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, pageRequest).stream().toList();
        } else {
            compilations = compilationRepository.findAll(pageRequest).stream().toList();
        }
        log.info("Получены подборки: {}", compilations);
        return compilations.stream().map(CompilationMapper::toCompilationDto).toList();
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(String.format("Compilation with id=%s was not found", compId)));
        log.info("Получена подборка: {}", compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }
}
