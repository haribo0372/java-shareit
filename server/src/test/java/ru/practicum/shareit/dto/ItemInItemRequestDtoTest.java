package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.item.ItemInItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemInItemRequestDtoTest {
    @Test
    public void equals_itemsShouldBeEquals() {
        ItemInItemRequestDto requestDto1 = new ItemInItemRequestDto(1L, "str-1", 3L);
        ItemInItemRequestDto requestDto2 = new ItemInItemRequestDto(1L, "str-2", 9L);
        assertThat(requestDto1.equals(requestDto2)).isTrue();

        requestDto2 = requestDto1;
        assertThat(requestDto1.equals(requestDto2)).isTrue();

    }

    @Test
    public void equals_itemsShouldNotEquals() {
        ItemInItemRequestDto requestDto1 = new ItemInItemRequestDto(2L, "str-1", 3L);
        ItemInItemRequestDto requestDto2 = new ItemInItemRequestDto(1L, "str-1", 3L);
        assertThat(requestDto1.equals(requestDto2)).isFalse();

        assertThat(requestDto1.equals(0)).isFalse();
        assertThat(requestDto1.equals(null)).isFalse();
    }
}
