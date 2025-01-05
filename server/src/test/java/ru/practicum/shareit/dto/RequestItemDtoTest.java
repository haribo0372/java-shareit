package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.item.RequestItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestItemDtoTest {

    @Autowired
    private JacksonTester<RequestItemDto> json;

    @Test
    void testSerialize() throws Exception {
        var dto = new RequestItemDto("item-1", "description-1", true, 1L);

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(dto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(dto.getRequestId().intValue());
    }

    @Test
    void testDeserialize() throws Exception {
        var jsonContent = "{\"name\": \"item-1\", \"description\": \"description-1\", \"available\": true, \"requestId\": 123}";

        var result = json.parse(jsonContent);

        assertThat(result.getObject().getName()).isEqualTo("item-1");
        assertThat(result.getObject().getDescription()).isEqualTo("description-1");
        assertThat(result.getObject().getAvailable()).isEqualTo(true);
        assertThat(result.getObject().getRequestId()).isEqualTo(123L);
    }
}
