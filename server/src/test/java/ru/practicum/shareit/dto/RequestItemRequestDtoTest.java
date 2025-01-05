package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.RequestItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestItemRequestDtoTest {

    @Autowired
    private JacksonTester<RequestItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        var dto = new RequestItemRequestDto("description-1");

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
    }

    @Test
    void testDeserialize() throws Exception {
        var jsonContent = "{\"description\": \"description-1\"}";

        var result = json.parse(jsonContent);

        assertThat(result.getObject().getDescription()).isEqualTo("description-1");
    }
}

