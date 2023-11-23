package lux.fm.bookingservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import lux.fm.bookingservice.AbstractPostgresAwareTest;
import lux.fm.bookingservice.dto.booking.BookingRequestCreateDto;
import lux.fm.bookingservice.dto.booking.BookingRequestUpdateDto;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.dto.exception.ErrorResponseWrapper;
import lux.fm.bookingservice.model.Booking;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Sql(scripts = {"classpath:database/bookings/delete-accommodation.sql",
        "classpath:database/users/delete-user.sql",
        "classpath:database/bookings/delete-bookings.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"classpath:database/users/add-user.sql",
        "classpath:database/bookings/add-accommodation.sql",
        "classpath:database/bookings/add-bookings.sql"})
public class BookingControllerTest extends AbstractPostgresAwareTest {
    protected static MockMvc mockMvc;
    private static ObjectMapper objectMapper;

    @BeforeAll
    @SneakyThrows
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Sql(scripts = {"classpath:database/bookings/delete-bookings.sql"})
    @Test
    @DisplayName("Create new booking")
    @WithMockUser(username = "test@check.com", roles = {"CUSTOMER"})
    void create_validBooking_ok() throws Exception {
        BookingRequestCreateDto requestDto = new BookingRequestCreateDto(
                1L,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );

        MvcResult mvcResult = mockMvc.perform(post("/api/bookings")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        BookingResponseDto responseDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                BookingResponseDto.class
        );
        assertThat(responseDto).isNotNull()
                .hasFieldOrPropertyWithValue("status", Booking.Status.PENDING)
                .hasFieldOrPropertyWithValue("accommodationId", 1L)
                .hasFieldOrPropertyWithValue("checkIn", requestDto.checkIn())
                .hasFieldOrPropertyWithValue("checkOut", requestDto.checkOut());
    }

    @Test
    @DisplayName("Find by id")
    @WithMockUser(username = "test@check.com", roles = {"CUSTOMER"})
    void findUserBookingById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/bookings/" + 1L))
                .andExpect(status().isOk())
                .andReturn();
        BookingResponseDto responseDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                BookingResponseDto.class
        );
        assertThat(responseDto).isNotNull()
                .hasFieldOrPropertyWithValue("status", Booking.Status.PENDING)
                .hasFieldOrPropertyWithValue("accommodationId", 1L)
                .hasFieldOrPropertyWithValue("checkIn", LocalDate.of(2023, 11, 24))
                .hasFieldOrPropertyWithValue("checkOut", LocalDate.of(2023, 11, 25));
    }

    @Test
    @DisplayName("Find all user's bookings ")
    @WithMockUser(username = "test@check.com", roles = {"CUSTOMER"})
    void findUserBookings() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/bookings/my"))
                .andExpect(status().isOk())
                .andReturn();
        List<BookingResponseDto> response = Arrays.stream(
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                        BookingResponseDto[].class)).toList();
        assertThat(response).isNotNull()
                .hasSize(3)
                .element(1)
                .hasFieldOrPropertyWithValue("status", Booking.Status.PENDING)
                .hasFieldOrPropertyWithValue("accommodationId", 1L)
                .hasFieldOrPropertyWithValue("checkIn", LocalDate.of(2023, 11, 24))
                .hasFieldOrPropertyWithValue("checkOut", LocalDate.of(2023, 11, 25));
        assertThat(response).isNotNull()
                .hasSize(3)
                .element(0)
                .hasFieldOrPropertyWithValue("status", Booking.Status.CONFIRMED)
                .hasFieldOrPropertyWithValue("accommodationId", 1L)
                .hasFieldOrPropertyWithValue("checkIn", LocalDate.of(2023, 11, 26))
                .hasFieldOrPropertyWithValue("checkOut", LocalDate.of(2023, 11, 27));
    }

    @Test
    @DisplayName("Find by user id and status")
    @WithMockUser(username = "test@check.com", roles = {"MANAGER"})
    void findBookingByUserIdAndStatus() throws Exception {
        Long id = 1L;
        Booking.Status status = Booking.Status.PENDING;
        String requestUrl = UriComponentsBuilder.fromUriString("/api/bookings")
                .queryParam("id", id)
                .queryParam("status", status)
                .buildAndExpand(id)
                .toUriString();
        MvcResult mvcResult = mockMvc.perform(get(requestUrl))
                .andExpect(status().isOk())
                .andReturn();
        List<BookingResponseDto> response = Arrays.stream(
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                        BookingResponseDto[].class)).toList();
        assertThat(response)
                .hasSize(2);

        assertThat(response).isNotNull()
                .element(0)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("status", Booking.Status.PENDING)
                .hasFieldOrPropertyWithValue("accommodationId", 1L)
                .hasFieldOrPropertyWithValue("checkIn", LocalDate.of(2023, 11, 24))
                .hasFieldOrPropertyWithValue("checkOut", LocalDate.of(2023, 11, 25));

        assertThat(response).isNotNull()
                .element(1)
                .hasFieldOrPropertyWithValue("id", 3L)
                .hasFieldOrPropertyWithValue("status", Booking.Status.PENDING)
                .hasFieldOrPropertyWithValue("accommodationId", 1L)
                .hasFieldOrPropertyWithValue("checkIn", LocalDate.of(2023, 11, 28))
                .hasFieldOrPropertyWithValue("checkOut", LocalDate.of(2023, 11, 29));
    }

    @Test
    @DisplayName("Update booking ")
    @WithMockUser(username = "test@check.com", roles = {"CUSTOMER"})
    void updateBooking() throws Exception {
        long id = 1L;
        BookingRequestUpdateDto updateDto = new BookingRequestUpdateDto(
                LocalDate.now().plusYears(1),
                LocalDate.now().plusDays(1).plusYears(1)
        );
        MvcResult mvcResult = mockMvc.perform(put("/api/bookings/" + id)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto responseDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                BookingResponseDto.class
        );
        assertThat(responseDto).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("status", Booking.Status.PENDING)
                .hasFieldOrPropertyWithValue("accommodationId", 1L)
                .hasFieldOrPropertyWithValue("checkIn", LocalDate.now().plusYears(1))
                .hasFieldOrPropertyWithValue("checkOut", LocalDate.now().plusDays(1).plusYears(1));
    }

    @Test
    @DisplayName("Delete non existing booking ")
    @WithMockUser(username = "test@check.com", roles = {"CUSTOMER"})
    void deleteNotExistingBooking() throws Exception {
        MvcResult mvcResult = mockMvc.perform(delete("/api/bookings/" + 1000L))
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponseWrapper error = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );
        assertThat(error.details()).isEqualTo("Booking with such id doesn't exist: " + 1000L);
    }

    @Test
    @DisplayName("Create conflicting booking")
    @WithMockUser(username = "test@check.com", roles = {"CUSTOMER"})
    void createConflictingBooking() throws Exception {
        BookingRequestCreateDto requestDto = new BookingRequestCreateDto(
                1L,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );
        MvcResult mvcResult = mockMvc.perform(post("/api/bookings")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponseWrapper error = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );
        assertThat(error.details()).isEqualTo("The accommodation isn't available with id: 1");
    }

    @Test
    @DisplayName("Delete pending booking")
    @WithMockUser(username = "test@check.com", roles = {"CUSTOMER"})
    void deletePendingBooking() throws Exception {
        long id = 2;
        MvcResult mvcResult = mockMvc.perform(delete("/api/bookings/" + id))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponseWrapper error = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );
        assertThat(error.details()).isEqualTo("Booking can't be deleted on this stage");
    }
}
