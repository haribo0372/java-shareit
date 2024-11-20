package ru.practicum.shareit.user.service;

import ru.practicum.shareit.base.service.BaseService;
import ru.practicum.shareit.user.dto.RequestCreateUserDto;
import ru.practicum.shareit.user.dto.RequestUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService extends BaseService<User, Long> {
    UserDto create(RequestCreateUserDto user);

    UserDto update(Long userId, RequestUpdateUserDto user);

    Optional<UserDto> findByEmail(String email);

    UserDto findUserById(Long id);

    Collection<UserDto> getAll();
}
