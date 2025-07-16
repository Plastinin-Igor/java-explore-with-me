package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.module.Compilation;
import ru.practicum.event.mapper.EventMapper;

import java.util.Collections;

public final class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setEvents(compilation.getEvents() != null ?
                compilation.getEvents()
                        .stream()
                        .map(EventMapper::toEventShortDto)
                        .toList()
                : Collections.emptyList());

        return compilationDto;
    }

    public static Compilation toCompilationFromNew(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(newCompilationDto.getPinned() != null ? newCompilationDto.getPinned() : false);
        compilation.setTitle(newCompilationDto.getTitle());

        return compilation;
    }


    public static Compilation toCompilationFromUpdate(UpdateCompilationRequest updateComp, Compilation compilation) {

        if (updateComp.hasTitle()) {
            compilation.setTitle(updateComp.getTitle());
        }

        if (updateComp.hasPinned()) {
            compilation.setPinned(updateComp.getPinned());
        }

        return compilation;
    }


}
