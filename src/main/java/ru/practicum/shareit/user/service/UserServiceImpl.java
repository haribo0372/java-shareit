package ru.practicum.shareit.user.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.base.service.BaseInMemoryService;
import ru.practicum.shareit.user.dto.RequestCreateUserDto;
import ru.practicum.shareit.user.dto.RequestUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl extends BaseInMemoryService<User> implements UserService {
    public UserServiceImpl() {
        super(User.class);
    }

    @Override
    public UserDto create(RequestCreateUserDto userDto) {
        User user = UserMapper.fromDto(userDto);
        checkingValidationEmail(user);
        User savedUser = super.save(user);
        log.info("User{id={}} успешно сохранен", savedUser.getId());
        return this.toDto(savedUser);
    }

    @Override
    public UserDto update(Long userId, RequestUpdateUserDto userDto) {
        User user = UserMapper.fromDto(userId, userDto);
        User storageUser = super.findById(user.getId());

        if (user.getName() != null && !user.getName().isBlank())
            storageUser.setName(user.getName());
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            checkingValidationEmail(user);
            storageUser.setEmail(user.getEmail());
        }
        log.info("User{id={}} успешно обновлен", storageUser.getId());

        return this.toDto(storageUser);
    }

    @Override
    public Optional<UserDto> findByEmail(String email) {
        Optional<User> anyUser = super.findAll().stream().filter(user -> user.getEmail().equals(email)).findAny();
        return anyUser.map(this::toDto);
    }

    @Override
    public UserDto findUserById(Long id) {
        return this.toDto(super.findById(id));
    }

    @Override
    public Collection<UserDto> getAll() {
        return this.toDto(super.findAll());
    }

    private void checkingValidationEmail(User user) {
        if (findByEmail(user.getEmail()).isPresent())
            throw new ValidationException("Пользователь с таким email уже существует");
    }

    private UserDto toDto(User user) {
        return UserMapper.toUserDto(user);
    }

    private Collection<UserDto> toDto(Collection<User> users) {
        return UserMapper.toUserDto(users);
    }
}
