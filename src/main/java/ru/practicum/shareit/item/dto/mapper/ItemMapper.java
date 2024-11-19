package ru.practicum.shareit.item.dto.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCreateItemDto;
import ru.practicum.shareit.item.dto.RequestUpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.util.enums.ItemStatus;

import java.util.Collection;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                fromItemStatusToBoolean(item.getStatus()),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Collection<ItemDto> toItemDto(Collection<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).toList();
    }

    public static Item fromDto(RequestCreateItemDto itemDto) {
        return new Item(
                null,
                itemDto.getName(),
                itemDto.getDescription(),
                fromBooleanToItemStatus(itemDto.getAvailable()),
                null,
                null);
    }

    public static Item fromDto(Long itemId, RequestUpdateItemDto itemDto) {
        return new Item(
                itemId,
                itemDto.getName(),
                itemDto.getDescription(),
                fromBooleanToItemStatus(itemDto.getStatus()),
                null,
                null);
    }

    private static ItemStatus fromBooleanToItemStatus(Boolean condition) {
        if (condition == null)
            return null;
        return condition ? ItemStatus.AVAILABLE : ItemStatus.UNAVAILABLE;
    }

    private static Boolean fromItemStatusToBoolean(ItemStatus itemStatus) {
        if (itemStatus == null)
            return null;
        return itemStatus == ItemStatus.AVAILABLE;
    }
}