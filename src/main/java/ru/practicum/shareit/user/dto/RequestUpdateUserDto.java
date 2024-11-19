package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestUpdateUserDto {
    private Long id;
    private String name;

    @Email(message = "Указанный адрес email имеет неверный формат")
    private String email;
}
