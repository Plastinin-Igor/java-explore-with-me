package ru.practicum.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.location.dto.LocationDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    @NotNull
    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;

    @NotNull
    @Positive
    private Long category;

    @NotNull
    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;

    @Past
    @NotNull
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    private boolean paid = false;

    private int participantLimit = 0;

    private boolean requestModeration = true;

    @NotNull
    @NotBlank
    @Length(min = 3, max = 120)
    private String title;

}
