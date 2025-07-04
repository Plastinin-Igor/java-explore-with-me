package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {

    private List<EventShortDto> events;

    @NotNull
    @Positive
    private Long id;

    @NotNull
    private boolean pinned;

    @NotNull
    @NotBlank
    private String title;
}
