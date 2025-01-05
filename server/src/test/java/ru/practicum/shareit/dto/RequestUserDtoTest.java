package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.RequestUserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestUserDtoTest {

    @Autowired
    private JacksonTester<RequestUserDto> json;

    @Test
    void testSerialize() throws Exception {
        var dto = new RequestUserDto("name-1", "email1@example.com");

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(dto.getEmail());
    }

    @Test
    void testDeserialize() throws Exception {
        var jsonContent = "{\"name\": \"name-1\", \"email\": \"email1@example.com\"}";

        var result = json.parse(jsonContent);

        assertThat(result.getObject().getName()).isEqualTo("name-1");
        assertThat(result.getObject().getEmail()).isEqualTo("email1@example.com");
    }
}

