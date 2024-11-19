package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.base.model.BaseModel;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class User extends BaseModel {
    private String name;
    private String email;

    public User(Long id, String name, String email) {
        super(id);
        this.name = name;
        this.email = email;
    }
}
