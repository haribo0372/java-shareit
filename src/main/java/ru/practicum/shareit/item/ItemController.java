package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestCreateItemDto;
import ru.practicum.shareit.item.dto.RequestUpdateItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> findAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toItemDto(itemService.findAllItemsByUserId(userId));
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable Long itemId) {
        Item foundItem = itemService.findById(itemId);
        return ItemMapper.toItemDto(foundItem);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchByNameAndDescription(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam("text") String text){
        return ItemMapper.toItemDto(itemService.searchByNameAndDescription(userId, text));
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody RequestCreateItemDto itemDto) {
        Item createdItem = itemService.create(userId, ItemMapper.fromDto(itemDto));
        return ItemMapper.toItemDto(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId,
                          @Valid @RequestBody RequestUpdateItemDto itemDto) {
        Item createdItem = itemService.update(userId, ItemMapper.fromDto(itemId, itemDto));
        return ItemMapper.toItemDto(createdItem);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteById(@RequestParam Long itemId) {
        itemService.deleteById(itemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
