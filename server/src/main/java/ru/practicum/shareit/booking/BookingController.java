package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestCreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.UserIdMissingException;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public Collection<BookingDto> getAllByBookerId(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                   @RequestParam(required = false, defaultValue = "all") String state) {
        validateUserId(userId);
        return bookingService.getAllByUserId(userId, state);
    }

    @PostMapping
    public BookingDto create(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                             @RequestBody RequestCreateBookingDto requestCreateBookingDto) {
        validateUserId(userId);
        return bookingService.create(userId, requestCreateBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam boolean approved) {
        validateUserId(userId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                              @PathVariable Long bookingId) {
        validateUserId(userId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllByItemOwnerId(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                      @RequestParam(required = false, defaultValue = "all") String state) {
        validateUserId(userId);
        return bookingService.getAllByItemOwnerId(userId, state);
    }

    private void validateUserId(Long userId) {
        if (userId == null)
            throw new UserIdMissingException("Заголовок \"X-Sharer-User-Id\" отсутствует");
    }
}
