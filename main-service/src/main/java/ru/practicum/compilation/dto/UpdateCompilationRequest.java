package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {

    private List<Long> events;

    private Boolean pinned;

    @Length(min = 1, max = 50)
    private String title;

    public boolean hasEvents() {
        return !events.isEmpty();
    }

    public boolean hasTitle() {
        return !(title == null || title.isBlank());
    }
}
