package ru.practicum.shareit.item.service;

import ru.practicum.shareit.base.service.BaseService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCreateItemDto;
import ru.practicum.shareit.item.dto.RequestUpdateItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService extends BaseService<Item, Long> {
    ItemDto create(Long userId, RequestCreateItemDto item);

    ItemDto update(Long userId, Long itemId, RequestUpdateItemDto item);

    ItemDto findItemById(Long itemId);

    Collection<ItemDto> findAllItemsByUserId(Long userId);

    Collection<ItemDto> searchByNameAndDescription(Long userId, String text);
}
