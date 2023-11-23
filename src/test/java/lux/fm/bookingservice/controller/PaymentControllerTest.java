package lux.fm.bookingservice.controller;

import static lux.fm.bookingservice.model.Payment.Status.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import lux.fm.bookingservice.AbstractPostgresAwareTest;
import lux.fm.bookingservice.dto.payment.CreatePaymentRequestDto;
import lux.fm.bookingservice.dto.payment.PaymentResponseDto;
import lux.fm.bookingservice.dto.payment.PaymentWithoutSessionDto;
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

@Sql(scripts = "classpath:database/payments/add-all-entities.sql")
@Sql(scripts = "classpath:database/payments/delete-all-entities.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
public class PaymentControllerTest extends AbstractPostgresAwareTest {
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

    @Test
    @DisplayName("creating payment session")
    @WithMockUser(username = "test@check.com", roles = {"CUSTOMER"})
    void createPaymentSession_WithValidData_ShouldReturn() throws Exception {
        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto(
                1L
        );
        MvcResult mvcResult = mockMvc.perform(post("/api/payments")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PaymentResponseDto responseDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                PaymentResponseDto.class);
        assertThat(responseDto)
                .hasFieldOrPropertyWithValue("bookingId", 1L)
                .hasFieldOrPropertyWithValue("status", PENDING)
                .hasFieldOrProperty("sessionUrl").isNotNull()
                .hasFieldOrPropertyWithValue("amountToPay", BigDecimal.valueOf(20));

    }

    @Test
    @DisplayName("creating payment with invalid data")
    @WithMockUser(username = "test@check.com", roles = {"CUSTOMER"})
    void createPaymentSession_WithinValidBookingId_BadRequest() throws Exception {
        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto(
                100L
        );
        mockMvc.perform(post("/api/payments")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @DisplayName("creating payment with invalid data")
    @WithMockUser(username = "test@check.com", roles = {"CUSTOMER"})
    void createPaymentSession_WithinValidBookingStatus_BadRequest() throws Exception {
        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto(
                100L
        );
        mockMvc.perform(post("/api/payments")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @Sql(scripts = "classpath:database/payments/add-payment.sql")
    @DisplayName("creating payment with invalid data")
    @WithMockUser(username = "test@check.com", roles = {"MANAGER"})
    void findPayment_ValidData_Success() throws Exception {
        Long userId = 1L;
        MvcResult mvcResult = mockMvc.perform(get("/api/payments")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<PaymentWithoutSessionDto> response = Arrays.stream(
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                        PaymentWithoutSessionDto[].class)).toList();

        assertThat(response).isNotNull()
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("bookingId", 1L)
                .hasFieldOrPropertyWithValue("status", PENDING)
                .hasFieldOrPropertyWithValue("amountToPay", BigDecimal.valueOf(20));
    }

}
