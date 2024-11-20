package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.base.service.BaseInMemoryService;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCreateItemDto;
import ru.practicum.shareit.item.dto.RequestUpdateItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

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
    public ItemDto update(Long userId, Long itemId, RequestUpdateItemDto itemDto) {
        Item item = ItemMapper.fromDto(itemId, itemDto);
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
        return this.toDto(storageItem);
    }

    @Override
    public ItemDto findItemById(Long itemId) {
        return this.toDto(this.findById(itemId));
    }

    @Override
    public Collection<ItemDto> findAllItemsByUserId(Long userId) {
        List<ItemDto> foundItems = super.findAll().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(this::toDto)
                .toList();
        log.debug("Все Item, принадлежающие пользователю User{id{}} успешно найдены", userId);
        return foundItems;
    }

    @Override
    public Collection<ItemDto> searchByNameAndDescription(Long userId, String text) {
        Collection<ItemDto> foundItems = this.search(userId,
                itemDto -> (itemDto.getName().toLowerCase().contains(text.toLowerCase()) ||
                        itemDto.getDescription().toLowerCase().contains(text.toLowerCase())) && itemDto.getStatus());

        log.debug("Все Item's, принадлежащие пользователю User{id={}} и имеющие подстроку {} " +
                "в поле name или description успешно найдены", userId, text);
        return foundItems;
    }

    @Override
    public ItemDto create(Long userId, RequestCreateItemDto requestItemDto) {
        Item item = ItemMapper.fromDto(requestItemDto);
        User foundUser = userService.findById(userId);
        item.setOwner(foundUser);
        Item savedItem = super.save(item);
        log.info("Item{id={}} успешно сохранен у пользователя User{id={}}", savedItem.getId(), userId);
        return this.toDto(savedItem);
    }

    private Collection<ItemDto> search(Long userId, Predicate<ItemDto> predicate) {
        return findAllItemsByUserId(userId).stream().filter(predicate).toList();
    }

    private ItemDto toDto(Item item) {
        return ItemMapper.toItemDto(item);
    }
}
