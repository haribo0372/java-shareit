package ru.practicum.shareit.util.enums;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.util.enums.State;

import static org.assertj.core.api.Assertions.assertThat;

public class StateTest {
    @Test
    public void fromString_shouldBeReturnNull() {
        assertThat(State.fromString(null)).isNull();
    }
}
