package ru.practicum.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.State;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {

    @NotNull
    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private CategoryDto category;

    @Positive
    private int confirmedRequests;

    private LocalDateTime createdOn;

    @NotNull
    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;

    @NotNull
    private LocalDateTime eventDate;

    @NotNull
    private Long id;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private LocationDto location;

    @NotNull
    private boolean paid;

    private int participantLimit;

    private LocalDateTime publishedOn;

    private boolean requestModeration;

    private State state;

    @NotNull
    @NotBlank
    private String title;

    private int views;


}
