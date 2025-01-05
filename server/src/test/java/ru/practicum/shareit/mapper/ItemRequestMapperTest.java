package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemRequestMapperTest {

    @Test
    public void toDto() {
        ItemRequest itemRequest = new ItemRequest(
                1L, "ItemRequest-1::/description",
                null, LocalDateTime.now().minusDays(1));

        ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequest);

        assertThat(itemRequestDto).isNotNull();
        assertThat(itemRequestDto.getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequestDto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequestDto.getCreated()).isEqualTo(itemRequest.getCreated());
    }

    @Test
    public void fromDto() {
        RequestItemRequestDto requestItemRequestDto = new RequestItemRequestDto("Description-1");
        ItemRequest request = ItemRequestMapper.fromDto(requestItemRequestDto);

        assertThat(request).isNotNull();
        assertThat(request.getDescription()).isEqualTo(requestItemRequestDto.getDescription());
    }
}
