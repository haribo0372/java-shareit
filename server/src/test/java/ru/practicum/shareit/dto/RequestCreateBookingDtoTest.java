package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.RequestCreateBookingDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestCreateBookingDtoTest {

    @Autowired
    private JacksonTester<RequestCreateBookingDto> json;

    @Test
    void testSerialize() throws Exception {
        var dto = new RequestCreateBookingDto(1L, LocalDateTime.of(2023, 1, 1, 12, 0), LocalDateTime.of(2023, 1, 2, 12, 0));

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(dto.getItemId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(dto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(dto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void testDeserialize() throws Exception {
        var jsonContent = "{\"itemId\": 123, \"start\": \"2023-01-01T12:00:00\", \"end\": \"2023-01-02T12:00:00\"}";

        var result = json.parse(jsonContent);

        assertThat(result.getObject().getItemId()).isEqualTo(123L);
        assertThat(result.getObject().getStart()).isEqualTo(LocalDateTime.of(2023, 1, 1, 12, 0));
        assertThat(result.getObject().getEnd()).isEqualTo(LocalDateTime.of(2023, 1, 2, 12, 0));
    }
}

