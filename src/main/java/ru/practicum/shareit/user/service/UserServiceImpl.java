package ru.practicum.shareit.user.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.base.service.BaseInMemoryService;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl extends BaseInMemoryService<User> implements UserService {
    public UserServiceImpl() {
        super(User.class);
    }

    @Override
    public User create(User user) {
        checkingValidationEmail(user);
        User savedUser = super.save(user);
        log.info("User{id={}} успешно сохранен", savedUser.getId());
        return savedUser;
    }

    @Override
    public User update(User user) {
        User storageUser = super.findById(user.getId());

        if (user.getName() != null && !user.getName().isBlank())
            storageUser.setName(user.getName());
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            checkingValidationEmail(user);
            storageUser.setEmail(user.getEmail());
        }
        log.info("User{id={}} успешно обновлен", storageUser.getId());

        return storageUser;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return super.findAll().stream().filter(user -> user.getEmail().equals(email)).findAny();
    }

    private void checkingValidationEmail(User user) {
        if (findByEmail(user.getEmail()).isPresent())
            throw new ValidationException("Пользователь с таким email уже существует");
    }
}
