package lux.fm.bookingservice.controller;

import static lux.fm.bookingservice.model.Accommodation.AccommodationType.HOUSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import lux.fm.bookingservice.AbstractPostgresAwareTest;
import lux.fm.bookingservice.dto.accommodation.AccommodationDto;
import lux.fm.bookingservice.dto.accommodation.CreateAccommodationRequestDto;
import lux.fm.bookingservice.dto.exception.ErrorResponseWrapper;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
        "classpath:database/accommodations/delete-all-accommodations.sql"},
         executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SqlMergeMode(MERGE)
public class AccommodationControllerTest extends AbstractPostgresAwareTest {
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
    @DisplayName("Create valid accommodation")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void createAccommodation_ValidRequestDto_Success() throws Exception {
        CreateAccommodationRequestDto requestDto = new CreateAccommodationRequestDto(
                HOUSE,
                "Ukraine",
                "2 floors, 5 rooms",
                List.of("Wi-Fi", "Conditioner", "Parking", "Coffee maker"),
                BigDecimal.valueOf(500),
                3
        );

        MvcResult result = mockMvc.perform(post("/api/accommodations")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        AccommodationDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AccommodationDto.class
        );

        assertThat(actual).isNotNull()
                .hasFieldOrPropertyWithValue("type", HOUSE)
                .hasFieldOrPropertyWithValue("location", "Ukraine")
                .hasFieldOrPropertyWithValue("size", "2 floors, 5 rooms")
                .hasFieldOrPropertyWithValue("amenities", List.of("Wi-Fi",
                        "Conditioner", "Parking", "Coffee maker"))
                .hasFieldOrPropertyWithValue("dailyRate", BigDecimal.valueOf(500))
                .hasFieldOrPropertyWithValue("availability", 3);
    }

    @Test
    @DisplayName("Create accommodation without availability")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createAccommodation_InvalidRequestDtoWithoutAccommodation_BadRequest() throws Exception {
        CreateAccommodationRequestDto requestDto = new CreateAccommodationRequestDto(
                HOUSE,
                "Ukraine",
                "2 floors, 5 rooms",
                List.of("Wi-Fi", "Conditioner", "Parking", "Coffee maker"),
                BigDecimal.valueOf(500),
                null
        );

        mockMvc.perform(post("/api/accommodations")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.details").value("availability must not be null"));
    }

    @Test
    @DisplayName("Find accommodation by existent id")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = "classpath:database/accommodations/add-one-accommodation.sql")
    void findById_WithExistentId_Success() throws Exception {
        Long id = 1L;

        MvcResult result = mockMvc.perform(get("/api/accommodations/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        AccommodationDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AccommodationDto.class
        );

        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("type", HOUSE)
                .hasFieldOrPropertyWithValue("location", "Ukraine")
                .hasFieldOrPropertyWithValue("size", "2 floors, 5 rooms")
                .hasFieldOrPropertyWithValue("dailyRate", BigDecimal.valueOf(500))
                .hasFieldOrPropertyWithValue("availability", 3)
                .extracting("amenities")
                .asInstanceOf(InstanceOfAssertFactories.list(String.class))
                .containsExactlyInAnyOrder("Wi-Fi",
                        "Conditioner", "Parking", "Coffee maker");
    }

    @Test
    @DisplayName("Find accommodation by non-existent id")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = "classpath:database/accommodations/add-one-accommodation.sql")
    void findById_WithNonExistedId_BadRequest() throws Exception {
        Long id = 100000L;

        MvcResult result = mockMvc.perform(get("/api/accommodations/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponseWrapper error = objectMapper.readValue(
                result.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );
        assertThat(error.details()).isEqualTo("No accommodation with id " + id);
    }

    @Test
    @DisplayName("Find all accommodations")
    @WithMockUser(username = "user", roles = "USER")
    @Sql(scripts = "classpath:database/accommodations/add-one-accommodation.sql")
    void findAll_ValidRequest_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/accommodations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<AccommodationDto> actual = Arrays.stream(
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        AccommodationDto[].class)).toList();

        assertThat(actual)
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("type", HOUSE)
                .hasFieldOrPropertyWithValue("location", "Ukraine")
                .hasFieldOrPropertyWithValue("size", "2 floors, 5 rooms")
                .hasFieldOrPropertyWithValue("dailyRate", BigDecimal.valueOf(500))
                .hasFieldOrPropertyWithValue("availability", 3)
                .extracting("amenities")
                .asInstanceOf(InstanceOfAssertFactories.list(String.class))
                .containsExactlyInAnyOrder("Wi-Fi",
                        "Conditioner", "Parking", "Coffee maker");
    }

    @Test
    @DisplayName("Update existent accommodation with valid request")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = "classpath:database/accommodations/add-one-accommodation.sql")
    void updateAccommodation_ValidRequestDtoAndExistentId_Success() throws Exception {
        Long id = 1L;
        CreateAccommodationRequestDto requestDto = new CreateAccommodationRequestDto(
                HOUSE,
                "Ukraine",
                "2 floors, 5 rooms",
                List.of(new String[]{"Wi-Fi", "Conditioner", "Parking", "Coffee maker"}),
                BigDecimal.valueOf(500),
                3
        );

        MvcResult result = mockMvc.perform(put("/api/accommodations/" + id)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        AccommodationDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AccommodationDto.class
        );

        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("type", HOUSE)
                .hasFieldOrPropertyWithValue("location", "Ukraine")
                .hasFieldOrPropertyWithValue("size", "2 floors, 5 rooms")
                .hasFieldOrPropertyWithValue("amenities", List.of("Wi-Fi",
                        "Conditioner", "Parking", "Coffee maker"))
                .hasFieldOrPropertyWithValue("dailyRate", BigDecimal.valueOf(500))
                .hasFieldOrPropertyWithValue("availability", 3);
    }

    @Test
    @DisplayName("Delete accommodation by existent id")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/accommodations/add-one-accommodation.sql")
    void deleteCategory_WithExistentId_Success() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/api/accommodations" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @DisplayName("Delete accommodation by id without Admin role")
    @WithMockUser(username = "admin", roles = "USER")
    @Sql(
            scripts = "classpath:database/accommodations/add-one-accommodation.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void deleteAccommodation_WithoutAdminRole_Forbidden() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/accommodations" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
