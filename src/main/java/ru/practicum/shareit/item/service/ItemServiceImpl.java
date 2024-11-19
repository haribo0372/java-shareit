package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.base.service.BaseInMemoryService;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
public class ItemServiceImpl extends BaseInMemoryService<Item> implements ItemService {
    private final UserService userService;

    @Autowired
    protected ItemServiceImpl(UserService userService) {
        super(Item.class);
        this.userService = userService;
    }

    @Override
    public Item update(Long userId, Item item) {
        User user = userService.findById(userId);
        Item storageItem = super.findById(item.getId());
        if (!storageItem.getOwner().equals(user))
            throw new AccessDeniedException(format("User{id=%d} не имеет доступ к Item{id=%d}", userId, item.getId()));

        if (item.getName() != null && !item.getName().isBlank())
            storageItem.setName(item.getName());
        if (item.getDescription() != null && !item.getDescription().isBlank())
            storageItem.setDescription(item.getDescription());
        if (item.getStatus() != null) {
            storageItem.setStatus(item.getStatus());
        }

        log.info("Item{id={}} у User{id={}} успешно обновлен", storageItem.getId(), userId);
        return storageItem;
    }

    @Override
    public Collection<Item> findAllItemsByUserId(Long userId) {
        List<Item> foundItems = super.findAll().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .toList();
        log.debug("Все Item, принадлежающие пользователю User{id{}} успешно найдены", userId);
        return foundItems;
    }

    @Override
    public Collection<Item> searchByNameAndDescription(Long userId, String text) {
        Set<Item> foundItemsByName = this.search(userId,
                item -> item.getName().toLowerCase().contains(text.toLowerCase()) && item.isAvailable());
        Set<Item> foundItemsByDescription = this.search(userId,
                item -> item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.isAvailable());

        foundItemsByDescription.addAll(foundItemsByName);
        log.debug("Все Item's, принадлежащие пользователю User{id={}} и имеющие подстроку {} " +
                "в поле name или description успешно найдены", userId, text);

        return foundItemsByDescription;
    }

    @Override
    public Item create(Long userId, Item item) {
        User foundUser = userService.findById(userId);
        item.setOwner(foundUser);
        Item savedItem = super.save(item);
        log.info("Item{id={}} успешно сохранен у пользователя User{id={}}", savedItem.getId(), userId);
        return savedItem;
    }

    private Set<Item> search(Long userId, Predicate<Item> predicate) {
        return findAllItemsByUserId(userId).stream().filter(predicate).collect(Collectors.toSet());
    }
}
