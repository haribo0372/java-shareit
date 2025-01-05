package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserEmailIsNotUnique;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    private final UserServiceImpl userService;
    private final UserRepository userRepository;

    private RequestUserDto requestUserDto;

    @BeforeEach
    public void setUp() {
        requestUserDto = new RequestUserDto("User-1", "user@gmail.com");
    }

    @Test
    public void create_userShouldBeCreated() {
        UserDto savedUser = userService.create(requestUserDto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo(requestUserDto.getName());
        assertThat(savedUser.getEmail()).isEqualTo(requestUserDto.getEmail());
    }

    @Test
    public void create_userShouldBeThrowUserEmailIsNotUnique() {
        userRepository.save(new User(null, "User-2", requestUserDto.getEmail()));

        assertThatThrownBy(() -> userService.create(requestUserDto))
                .isInstanceOf(UserEmailIsNotUnique.class)
                .hasMessage("Пользователь с таким email уже существует");
    }

    @Test
    public void update_nameAndEmailOfUserShouldBeUpdated() {
        String name = "User-1212-GGQC-1032";
        String email = "user1212@gmail.com";

        Long rootId = userRepository.save(new User(null, name, email)).getId();

        userService.update(rootId, requestUserDto);

        User updated = userRepository.findById(rootId).get();
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(rootId);
        assertThat(updated.getName()).isEqualTo(requestUserDto.getName());
        assertThat(updated.getEmail()).isEqualTo(requestUserDto.getEmail());
        assertThat(updated.getName()).isNotEqualTo(name);
        assertThat(updated.getEmail()).isNotEqualTo(email);
    }

    @Test
    public void update_onlyEmailOfUserShouldBeUpdatedWhereNameIsNull() {
        String name = "User-1";
        String email = "user1@gmail.com";

        Long rootId = userRepository.save(new User(null, name, email)).getId();
        requestUserDto.setName(null);
        userService.update(rootId, requestUserDto);
        User updated = userRepository.findById(rootId).get();
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(rootId);
        assertThat(updated.getName()).isEqualTo(name);
        assertThat(updated.getEmail()).isNotEqualTo(email);
        assertThat(updated.getEmail()).isEqualTo(requestUserDto.getEmail());
    }

    @Test
    public void update_onlyEmailOfUserShouldBeUpdatedWhereNameIsBlank() {
        String name = "User-1";
        String email = "user1@gmail.com";

        Long rootId = userRepository.save(new User(null, name, email)).getId();
        requestUserDto.setName("        ");
        userService.update(rootId, requestUserDto);
        User updated = userRepository.findById(rootId).get();
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(rootId);
        assertThat(updated.getName()).isEqualTo(name);
        assertThat(updated.getEmail()).isNotEqualTo(email);
        assertThat(updated.getEmail()).isEqualTo(requestUserDto.getEmail());
    }

    @Test
    public void update_onlyEmailShouldBeThrowUserEmailIsNotUnique() {
        String name = "User-1";
        String email = requestUserDto.getEmail();

        Long rootId = userRepository.save(new User(null, name, email)).getId();
        requestUserDto.setName(null);
        assertThatThrownBy(() -> userService.update(rootId, requestUserDto))
                .isInstanceOf(UserEmailIsNotUnique.class);
    }

    @Test
    public void update_onlyNameOfUserShouldBeUpdatedWhereEmailIsNull() {
        String name = "NAME FOR TEST";
        String email = "mail_for_test@gmail.com";

        Long rootId = userRepository.save(new User(null, name, email)).getId();
        requestUserDto.setEmail(null);
        userService.update(rootId, requestUserDto);

        User updated = userRepository.findById(rootId).get();
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(rootId);
        assertThat(updated.getName()).isNotEqualTo(name);
        assertThat(updated.getName()).isEqualTo(requestUserDto.getName());
        assertThat(updated.getEmail()).isEqualTo(email);
    }

    @Test
    public void update_onlyNameOfUserShouldBeUpdatedWhereEmailIsBlank() {
        String name = "NAME FOR TEST";
        String email = "mail_for_test@gmail.com";

        Long rootId = userRepository.save(new User(null, name, email)).getId();
        requestUserDto.setEmail("        ");
        userService.update(rootId, requestUserDto);

        User updated = userRepository.findById(rootId).get();
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(rootId);
        assertThat(updated.getName()).isNotEqualTo(name);
        assertThat(updated.getName()).isEqualTo(requestUserDto.getName());
        assertThat(updated.getEmail()).isEqualTo(email);
    }

    @Test
    public void update_shouldThrowUserEmailIsNotUnique() {
        User storageUser1 = userRepository.save(new User(null, "User-1", "user1@gmail.com"));
        User storageUser2 = userRepository.save(new User(null, "User-2", "user2@gmail.com"));

        requestUserDto.setEmail(storageUser1.getEmail());
        assertThatThrownBy(() -> userService.update(storageUser2.getId(), requestUserDto))
                .isInstanceOf(UserEmailIsNotUnique.class);
    }

    @Test
    public void update_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> userService.update(999L, requestUserDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void findUserById_shouldReturnUser() {
        User user = new User(null, "User-1", "user1@gmail.com");
        Long userId = userRepository.save(user).getId();
        UserDto foundUser = userService.findUserById(userId);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(userId);
        assertThat(foundUser.getName()).isEqualTo(user.getName());
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void findUserById_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> userService.findUserById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void findById_shouldReturnUser() {
        User user = new User(null, "User-1", "user1@gmail.com");
        Long userId = userRepository.save(user).getId();
        User foundUser = userService.findById(userId);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(userId);
        assertThat(foundUser.getName()).isEqualTo(user.getName());
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void findById_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void deleteById_userShouldBeDeleted() {
        User user = new User(null, "User-1", "user1@gmail.com");
        Long userId = userRepository.save(user).getId();
        assertThat(userRepository.findById(userId).isPresent()).isTrue();

        assertDoesNotThrow(() -> userService.deleteById(userId));
        assertThat(userRepository.findById(userId).isPresent()).isFalse();
    }

    @Test
    public void deleteById_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> userService.deleteById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void findAll_shouldReturnUser() {
        Set<Long> expectedIds = new HashSet<>();
        expectedIds.add(userRepository.save(new User(null, "User-1", "user1@gmail.com")).getId());
        expectedIds.add(userRepository.save(new User(null, "User-2", "user2@gmail.com")).getId());

        Collection<User> users = userService.findAll();

        assertThat(users).isNotNull();
        assertThat(users).isNotEmpty();
        assertThat(users.size()).isEqualTo(expectedIds.size());
        assertThat(users.stream().map(User::getId).collect(Collectors.toSet())).isEqualTo(expectedIds);
    }

    @Test
    public void checkExistsById_shouldBeSuccessfully() {
        Long userId = userRepository.save(new User(null, "User-1", "user1@gmail.com")).getId();
        assertDoesNotThrow(() -> userService.checkExistsById(userId));
    }

    @Test
    public void checkExistsById_shouldThrowNotFoundException() {
        Long userId = 999L;
        assertThatThrownBy(() -> userService.checkExistsById(userId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void existsById_shouldReturnTrue() {
        Long userId = userRepository.save(new User(null, "User-1", "user1@gmail.com")).getId();
        assertThat(userService.existsById(userId)).isTrue();
    }

    @Test
    public void existsById_shouldReturnFalse() {
        assertThat(userService.existsById(999L)).isFalse();
    }

    @Test
    public void findByEmail_shouldReturnPresentOpt() {
        User user = new User(null, "User-1", "user1@gmail.com");
        User savedUser = userRepository.save(user);
        String userEmail = user.getEmail();

        Optional<UserDto> foundUser = userService.findByEmail(userEmail);
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.get().getName()).isEqualTo(user.getName());
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void findByEmail_shouldReturnEmptyOpt() {
        String userEmail = "user@example.com";
        Optional<UserDto> foundUser = userService.findByEmail(userEmail);
        assertThat(foundUser.isEmpty()).isTrue();
    }

    @Test
    public void getAll_shouldReturnUsers() {
        User user = new User(null, "User-1", "user1@gmail.com");
        userRepository.save(user);

        Collection<UserDto> users = userService.getAll();
        assertThat(users).isNotNull();
        assertThat(users).isNotEmpty();
        assertThat(users.size()).isEqualTo(1);
    }

    @Test
    public void getAll_shouldReturnEmptyCollectionOfUsers() {
        Collection<UserDto> users = userService.getAll();
        assertThat(users).isNotNull();
        assertThat(users).isEmpty();
    }
}
