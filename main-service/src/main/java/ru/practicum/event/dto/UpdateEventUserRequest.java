package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;
import ru.practicum.event.model.StateAction;
import ru.practicum.location.dto.LocationDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest {

    @Length(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Length(min = 20, max = 7000)
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero
    private int participantLimit;

    private Boolean requestModeration;

    private StateAction stateAction;

    @Length(min = 3, max = 120)
    private String title;


    public boolean hasAnnotation() {
        return !(annotation == null || annotation.isBlank());
    }

    public boolean hasCategory() {
        return !(category == null || category == 0);
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasEventDate() {
        return !(eventDate == null);
    }

    public boolean hasLocation() {
        return !(location == null);
    }

    public boolean hasParticipantLimit() {
        return !(participantLimit == 0);
    }

    public boolean hasTitle() {
        return !(title == null || title.isBlank());
    }


}
