package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestCreateUserDto {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;

    @NotBlank(message = "Email пользователя не может быть пустым")
    @Email(message = "Указанный адрес email имеет неверный формат")
    private String email;
}
