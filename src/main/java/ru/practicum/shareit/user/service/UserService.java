package ru.practicum.shareit.user.service;

import ru.practicum.shareit.base.service.BaseService;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserService extends BaseService<User, Long> {
    User create(User user);
    User update(User user);
    Optional<User> findByEmail(String email);
}
