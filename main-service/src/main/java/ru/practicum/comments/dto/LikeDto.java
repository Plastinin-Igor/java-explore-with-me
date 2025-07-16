package ru.practicum.comments.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {

    private Boolean like;

    private Boolean dislike;

    @AssertTrue(message = "Невозможно одновременно поставить лайк и дизлайк.")
    public boolean isCorrectSetLikeDislike() {
        return !(like != null && like && dislike != null && dislike);
    }
}
