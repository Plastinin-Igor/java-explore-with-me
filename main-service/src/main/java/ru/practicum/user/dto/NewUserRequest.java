package ru.practicum.user.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @NotBlank
    @Email
    private String email;

    @NotNull
    @NotBlank
    @Length(min = 2, max = 250)
    private String name;
}
