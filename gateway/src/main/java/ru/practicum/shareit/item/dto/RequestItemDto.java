package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestItemDto {
    @NotBlank(message = "Поле name должно быть задано")
    private String name;

    @NotBlank(message = "Поле description должно быть задано")
    private String description;

    @NotNull(message = "Поле available должно быть указано")
    private Boolean available;

    private Long requestId;
}
