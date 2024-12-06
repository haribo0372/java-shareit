package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.enums.BookerStatus;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // by Booker
    Collection<Booking> findAllByBookerId(Long bookerId);

    Collection<Booking> findAllByBookerIdAndStartDateTimeIsBeforeAndEndDateTimeIsAfter(Long bookerId, LocalDateTime dateTime1, LocalDateTime dateTime2);

    Collection<Booking> findAllByBookerIdAndStartDateTimeIsAfter(Long bookerId, LocalDateTime dateTime);

    boolean existsByBookerIdAndItemIdAndEndDateTimeIsBefore(Long bookerId, Long itemId, LocalDateTime dateTime);

    Collection<Booking> findAllByBookerIdAndEndDateTimeIsBefore(Long bookerId, LocalDateTime dateTime);

    Collection<Booking> findAllByBookerIdAndStatusEquals(Long bookerId, BookerStatus status);

    // by Owner
    Collection<Booking> findAllByItemOwnerId(Long bookerId);

    Collection<Booking> findAllByItemOwnerIdAndStartDateTimeIsBeforeAndEndDateTimeIsAfter(Long ownerId, LocalDateTime dateTime1, LocalDateTime dateTime2);

    Collection<Booking> findAllByItemOwnerIdAndStartDateTimeIsAfter(Long ownerId, LocalDateTime dateTime);

    Collection<Booking> findAllByItemOwnerIdAndEndDateTimeIsBefore(Long ownerId, LocalDateTime dateTime);

    Collection<Booking> findAllByItemOwnerIdAndStatusEquals(Long ownerId, BookerStatus status);
}
