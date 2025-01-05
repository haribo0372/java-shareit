package ru.practicum.shareit.item.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestItemDto {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;

    public RequestItemDto(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
