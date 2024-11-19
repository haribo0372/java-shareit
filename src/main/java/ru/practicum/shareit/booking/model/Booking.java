package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.base.model.BaseModel;
import ru.practicum.shareit.booking.util.enums.BookerStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
public class Booking extends BaseModel {
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookerStatus status;
}
