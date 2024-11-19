package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.base.model.BaseModel;
import ru.practicum.shareit.item.util.enums.ItemStatus;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Item extends BaseModel {
    private String name;
    private String description;
    private ItemStatus status;
    private User owner;
    private ItemRequest request;

    public Item(Long id, String name, String description, ItemStatus status, User owner, ItemRequest request) {
        super(id);
        this.name = name;
        this.description = description;
        this.status = status;
        this.owner = owner;
        this.request = request;
    }

    public boolean isAvailable(){
        return status == ItemStatus.AVAILABLE;
    }
}
