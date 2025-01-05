package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestCreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.util.enums.BookerStatus;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.util.enums.ItemStatus;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    private final BookingServiceImpl bookingService;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    User owner;
    User booker;
    Item item;
    Booking currentBooking;
    Booking futureAndRejectedBooking;
    Booking pastAndWaitingBooking;

    @BeforeEach
    public void setUp() {
        owner = new User(null, "Owner-1", "owner@example.com");
        booker = new User(null, "Booker-1", "booker@example.com");
        item = new Item(null, "Item-1", "Item-1-description", ItemStatus.AVAILABLE, owner, null);

        LocalDateTime dateTimeNow = LocalDateTime.now();

        currentBooking = new Booking(
                null, dateTimeNow.minusDays(7),
                dateTimeNow.plusDays(3), item,
                booker, BookerStatus.APPROVED);

        futureAndRejectedBooking = new Booking(
                null, dateTimeNow.plusDays(4),
                dateTimeNow.plusDays(6), item,
                booker, BookerStatus.REJECTED);

        pastAndWaitingBooking = new Booking(
                null, dateTimeNow.minusDays(10),
                dateTimeNow.minusDays(9), item,
                booker, BookerStatus.WAITING);
    }

    @Test
    public void getAllByUserId_shouldReturnBookings() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        bookingRepository.save(currentBooking);
        bookingRepository.save(futureAndRejectedBooking);
        bookingRepository.save(pastAndWaitingBooking);

        List<List<Booking>> bookings = List.of(
                List.of(currentBooking, futureAndRejectedBooking, pastAndWaitingBooking),
                List.of(currentBooking),
                List.of(futureAndRejectedBooking),
                List.of(pastAndWaitingBooking),
                List.of(pastAndWaitingBooking),
                List.of(futureAndRejectedBooking)
        );

        List<String> states = List.of("ALL", "CURRENT", "FUTURE", "PAST", "WAITING", "REJECTED");

        IntStream.range(0, states.size()).forEach(index -> {
            List<BookingDto> foundBookings = new ArrayList<>(bookingService.getAllByUserId(booker.getId(), states.get(index)));
            List<Booking> expectedBookings = bookings.get(index);
            assertThat(foundBookings).isNotNull();
            assertThat(foundBookings).isNotEmpty();
            assertThat(foundBookings.size()).isEqualTo(expectedBookings.size());
            assertThat(collectionsWithBookingsAreEqual(foundBookings, expectedBookings)).isTrue();
        });
    }

    @Test
    public void getAllByUserId_shouldThrowValidationException() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        bookingRepository.save(currentBooking);
        bookingRepository.save(futureAndRejectedBooking);
        bookingRepository.save(pastAndWaitingBooking);

        assertThatThrownBy(() -> bookingService.getAllByUserId(booker.getId(), "INVALID_STATE"))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Нет соответствующего состояния для: INVALID_STATE");
    }

    @Test
    public void create_bookingShouldBeCreated() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        RequestCreateBookingDto requestBookingDto =
                new RequestCreateBookingDto(item.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        BookingDto bookingDto = bookingService.create(booker.getId(), requestBookingDto);
        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getId()).isNotNull();
        assertThat(bookingDto.getItem().getId()).isEqualTo(requestBookingDto.getItemId());
        assertThat(bookingDto.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    public void create_shouldThrowValidationException() {
        userRepository.save(owner);
        userRepository.save(booker);

        item.setStatus(ItemStatus.UNAVAILABLE);
        itemRepository.save(item);

        RequestCreateBookingDto requestBookingDto =
                new RequestCreateBookingDto(item.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        assertThatThrownBy(() -> bookingService.create(booker.getId(), requestBookingDto))
                .isInstanceOf(ValidationException.class)
                .hasMessage(format("Item{id=%d} не активен", item.getId()));
    }

    @Test
    public void approveBooking_shouldBeCompletedSuccessfully() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(currentBooking);

        assertDoesNotThrow(() -> bookingService.approveBooking(owner.getId(), currentBooking.getId(), true));
        Booking approvedBooking = bookingRepository.findById(currentBooking.getId()).get();
        assertThat(approvedBooking.getStatus()).isEqualTo(BookerStatus.APPROVED);

        assertDoesNotThrow(() -> bookingService.approveBooking(owner.getId(), currentBooking.getId(), false));
        Booking rejectedBooking = bookingRepository.findById(currentBooking.getId()).get();
        assertThat(rejectedBooking.getStatus()).isEqualTo(BookerStatus.REJECTED);
    }

    @Test
    public void approveBooking_shouldBeValidationException() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(currentBooking);

        assertThatThrownBy(() -> bookingService.approveBooking(999L, currentBooking.getId(), true))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void approveBooking_shouldBeAccessDeniedException() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(currentBooking);

        assertThatThrownBy(() -> bookingService.approveBooking(booker.getId(), currentBooking.getId(), true))
                .isInstanceOf(AccessDeniedException.class);
    }


    @Test
    public void getBookingById_shouldReturnBooking() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(currentBooking);

        BookingDto foundBookingByOwnerId = bookingService.getBookingById(owner.getId(), currentBooking.getId());
        assertThat(foundBookingByOwnerId.getId()).isEqualTo(currentBooking.getId());

        BookingDto foundBookingByBookerId = bookingService.getBookingById(booker.getId(), currentBooking.getId());
        assertThat(foundBookingByBookerId.getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    public void getBookingById_shouldThrowNotFoundException() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(currentBooking);

        assertThatThrownBy(() -> bookingService.getBookingById(999L, currentBooking.getId()))
                .isInstanceOf(NotFoundException.class);

        assertThatThrownBy(() -> bookingService.getBookingById(booker.getId(), 999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void getBookingById_shouldThrowAccessDeniedException() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(currentBooking);

        User userWithoutAccess = new User(null, "user_without_access", "userWA@example.com");
        userRepository.save(userWithoutAccess);

        assertThatThrownBy(() -> bookingService.getBookingById(userWithoutAccess.getId(), currentBooking.getId()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage(format("User{id=%d} не имеет доступа на просмотр Booking{id=%d}",
                        userWithoutAccess.getId(), currentBooking.getId()));
    }

    @Test
    public void getAllByItemOwnerId_shouldReturnBookings() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        bookingRepository.save(currentBooking);
        bookingRepository.save(futureAndRejectedBooking);
        bookingRepository.save(pastAndWaitingBooking);

        List<List<Booking>> bookings = List.of(
                List.of(currentBooking, futureAndRejectedBooking, pastAndWaitingBooking),
                List.of(currentBooking),
                List.of(futureAndRejectedBooking),
                List.of(pastAndWaitingBooking),
                List.of(pastAndWaitingBooking),
                List.of(futureAndRejectedBooking)
        );

        List<String> states = List.of("ALL", "CURRENT", "FUTURE", "PAST", "WAITING", "REJECTED");

        IntStream.range(0, states.size()).forEach(index -> {
            List<BookingDto> foundBookings = new ArrayList<>(bookingService.getAllByItemOwnerId(owner.getId(), states.get(index)));
            List<Booking> expectedBookings = bookings.get(index);
            assertThat(foundBookings).isNotNull();
            assertThat(foundBookings).isNotEmpty();
            assertThat(foundBookings.size()).isEqualTo(expectedBookings.size());
            assertThat(collectionsWithBookingsAreEqual(foundBookings, expectedBookings)).isTrue();
        });
    }

    @Test
    public void getAllByItemOwnerId_shouldThrowValidationException() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        bookingRepository.save(currentBooking);
        bookingRepository.save(futureAndRejectedBooking);
        bookingRepository.save(pastAndWaitingBooking);

        assertThatThrownBy(() -> bookingService.getAllByItemOwnerId(owner.getId(), "INVALID_STATE"))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Нет соответствующего состояния для: INVALID_STATE");
    }

    @Test
    public void getAllByItemOwnerId_shouldThrowNotFoundException() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        bookingRepository.save(currentBooking);
        bookingRepository.save(futureAndRejectedBooking);
        bookingRepository.save(pastAndWaitingBooking);

        assertThatThrownBy(() -> bookingService.getAllByItemOwnerId(999L, "ALL"))
                .isInstanceOf(NotFoundException.class);
    }

    private boolean collectionsWithBookingsAreEqual(List<BookingDto> bookings1, List<Booking> bookings2) {
        if (bookings1.size() != bookings2.size())
            return false;

        for (Booking booking : bookings2)
            if (bookings1.stream().filter(
                    i -> i.getId().equals(booking.getId())).findAny().isEmpty())
                return false;

        return true;
    }
}
