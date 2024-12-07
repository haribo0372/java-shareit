package ru.practicum.shareit.item.dto.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestUpdateItemDto {
    private Long id;
    private String name;
    private String description;
    @JsonProperty("available")
    private Boolean status;
}
