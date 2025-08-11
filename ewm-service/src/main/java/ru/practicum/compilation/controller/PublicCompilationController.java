package ru.practicum.compilation.controller;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.compilation.CompilationService;
import ru.practicum.compilation.dto.CompilationDto;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@AllArgsConstructor
public class PublicCompilationController {
    private final CompilationService service;

    @GetMapping
    private List<CompilationDto> getCompilations(@RequestParam @Nullable Boolean pinned,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    private CompilationDto getCompilationById(@PathVariable Long compId) {
        return service.getCompilationById(compId);
    }
}
