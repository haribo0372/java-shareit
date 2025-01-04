package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestCreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.util.enums.BookerStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private RequestCreateBookingDto requestBookingDto;
    private BookingDto responseBookingDto;

    @BeforeEach
    public void setUp() {
        requestBookingDto =
                new RequestCreateBookingDto(1L,
                        LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        responseBookingDto = new BookingDto(1L, requestBookingDto.getStart(),
                requestBookingDto.getEnd(), null, null, BookerStatus.APPROVED);
    }

    @Test
    public void getAllByBookerId_shouldReturnBookings() throws Exception {
        long userId = 1L;
        when(bookingService.getAllByUserId(eq(userId), anyString())).thenReturn(List.of(responseBookingDto));

        checkCorrectnessBooking(
                mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)), "$[0]", responseBookingDto
        );
    }

    @Test
    public void getAllByBookerId_shouldThrowUserIdMissingException() throws Exception {
        long userId = 1L;
        when(bookingService.getAllByUserId(eq(userId), anyString())).thenReturn(List.of(responseBookingDto));

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Неверный  запрос"))
                .andExpect(jsonPath("$.description").value("Заголовок \"X-Sharer-User-Id\" отсутствует"));
    }

    @Test
    public void create_bookingShouldBeCreated() throws Exception {
        long userId = 1L;
        when(bookingService.create(eq(userId), any(RequestCreateBookingDto.class))).thenReturn(responseBookingDto);

        checkCorrectnessBooking(
                mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBookingDto))), "$", responseBookingDto
        );
    }

    @Test
    public void create_shouldThrowUserIdMissingException() throws Exception {
        long userId = 1L;
        when(bookingService.create(eq(userId), any(RequestCreateBookingDto.class))).thenReturn(responseBookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Неверный  запрос"))
                .andExpect(jsonPath("$.description").value("Заголовок \"X-Sharer-User-Id\" отсутствует"));
    }

    @Test
    public void approveBooking_bookingShouldBeApproved() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        when(bookingService.approveBooking(eq(userId), eq(bookingId), any(Boolean.class)))
                .thenReturn(responseBookingDto);

        checkCorrectnessBooking(
                mockMvc.perform(patch("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(false))), "$", responseBookingDto
        );
    }

    @Test
    public void approveBooking_shouldThrowUserIdMissingException() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        when(bookingService.approveBooking(eq(userId), eq(bookingId), any(Boolean.class)))
                .thenReturn(responseBookingDto);

        mockMvc.perform(patch("/bookings/" + bookingId)
                        .param("approved", String.valueOf(false)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Неверный  запрос"))
                .andExpect(jsonPath("$.description").value("Заголовок \"X-Sharer-User-Id\" отсутствует"));
    }

    @Test
    public void getById_shouldReturnBooking() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        when(bookingService.getBookingById(eq(userId), eq(bookingId)))
                .thenReturn(responseBookingDto);

        checkCorrectnessBooking(
                mockMvc.perform(get("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId)), "$", responseBookingDto
        );
    }

    @Test
    public void getById_shouldThrowUserIdMissingException() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        when(bookingService.getBookingById(eq(userId), eq(bookingId)))
                .thenReturn(responseBookingDto);


        mockMvc.perform(get("/bookings/" + bookingId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Неверный  запрос"))
                .andExpect(jsonPath("$.description").value("Заголовок \"X-Sharer-User-Id\" отсутствует"));
    }

    @Test
    public void getAllByItemOwnerId_shouldReturnBookings() throws Exception {
        long userId = 1L;

        when(bookingService.getAllByItemOwnerId(eq(userId), anyString())).thenReturn(List.of(responseBookingDto));

        checkCorrectnessBooking(
                mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "all")), "$[0]", responseBookingDto
        );

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getAllByItemOwnerId_shouldThrowUserIdMissingException() throws Exception {
        long userId = 1L;

        when(bookingService.getAllByItemOwnerId(eq(userId), anyString())).thenReturn(List.of(responseBookingDto));


        mockMvc.perform(get("/bookings/owner")
                        .param("state", "all"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Неверный  запрос"))
                .andExpect(jsonPath("$.description").value("Заголовок \"X-Sharer-User-Id\" отсутствует"));
    }

    private void checkCorrectnessBooking(ResultActions resultActions, String prefixOfPath, BookingDto expectedBooking) throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(prefixOfPath + ".id").value(expectedBooking.getId()))
                .andExpect(jsonPath(prefixOfPath + ".start")
                        .value(expectedBooking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath(prefixOfPath + ".end")
                        .value(expectedBooking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath(prefixOfPath + ".item").value(expectedBooking.getItem()))
                .andExpect(jsonPath(prefixOfPath + ".booker").value(expectedBooking.getBooker()))
                .andExpect(jsonPath(prefixOfPath + ".status").value(expectedBooking.getStatus().toString().toUpperCase()));
    }
}
