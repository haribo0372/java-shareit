package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemMapperTest {
    @Test
    public void toItemDto() {
        Item item = new Item();
        assertThat(ItemMapper.toItemDto(item, null).getRequest()).isNull();

        ItemRequest request = new ItemRequest(1L, null, null, null);
        item.setRequest(request);
        assertThat(ItemMapper.toItemDto(item, null).getRequest()).isEqualTo(request.getId());
    }
}
