package ru.practicum.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

@RestController
@RequestMapping("/admin/compilations")
@Validated
@RequiredArgsConstructor
@Slf4j
public class CompilationControllerAdmin {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Получен запрос POST: /admin/compilations на добавление подборки {}.", newCompilationDto);
        CompilationDto compilationDto = compilationService.addCompilation(newCompilationDto);
        log.info("Подборка событий успешно добавлена в систему: {}.", compilationDto);
        return compilationDto;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Получен запрос DELETE: /admin/compilations/{} на удаление подборки.", compId);
        compilationService.deleteCompilation(compId);
        log.info("Подборка с id: {} успешно удалена из системы.", compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody @Valid UpdateCompilationRequest compilationRequest) {
        log.info("Получен запрос PATCH: /admin/compilations/{} на изменение подборки {}.", compId, compilationRequest);
        return compilationService.updateCompilation(compId, compilationRequest);
    }
}
