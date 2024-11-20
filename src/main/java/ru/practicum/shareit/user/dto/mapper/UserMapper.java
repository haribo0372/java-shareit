package ru.practicum.shareit.user.dto.mapper;

import ru.practicum.shareit.user.dto.RequestCreateUserDto;
import ru.practicum.shareit.user.dto.RequestUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static Collection<UserDto> toUserDto(Collection<User> users) {
        return users.stream().map(UserMapper::toUserDto).toList();
    }

    public static User fromDto(RequestCreateUserDto requestUserDto) {
        return new User(null, requestUserDto.getName(), requestUserDto.getEmail());
    }

    public static User fromDto(RequestUpdateUserDto requestUserDto) {
        return new User(requestUserDto.getId(), requestUserDto.getName(), requestUserDto.getEmail());
    }

    public static User fromDto(Long userId, RequestUpdateUserDto requestUserDto) {
        return new User(userId, requestUserDto.getName(), requestUserDto.getEmail());
    }
}
