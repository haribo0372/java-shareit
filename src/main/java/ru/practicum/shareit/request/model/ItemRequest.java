package ru.practicum.shareit.request.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.base.model.BaseModel;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ItemRequest extends BaseModel {
    private String description;
    private User requestor;
    private LocalDateTime created;

}
