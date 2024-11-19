package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.RequestCreateUserDto;
import ru.practicum.shareit.user.dto.RequestUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAll() {
        return UserMapper.toUserDto(userService.findAll());
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        User foundUser = userService.findById(userId);
        return UserMapper.toUserDto(foundUser);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody RequestCreateUserDto userDto) {
        User createdUser = userService.create(UserMapper.fromDto(userDto));
        return UserMapper.toUserDto(createdUser);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                          @Valid @RequestBody RequestUpdateUserDto userDto) {
        User createdUser = userService.update(UserMapper.fromDto(userId, userDto));
        return UserMapper.toUserDto(createdUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long userId) {
        userService.deleteById(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
