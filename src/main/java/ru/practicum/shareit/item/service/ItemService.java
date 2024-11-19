package ru.practicum.shareit.item.service;

import ru.practicum.shareit.base.service.BaseService;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService extends BaseService<Item, Long> {
    Item create(Long userId, Item item);

    Item update(Long userId, Item item);

    Collection<Item> findAllItemsByUserId(Long userId);

    Collection<Item> searchByNameAndDescription(Long userId, String text);
}
