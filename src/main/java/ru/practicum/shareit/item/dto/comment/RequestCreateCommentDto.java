package ru.practicum.shareit.item.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCreateCommentDto {
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
}
