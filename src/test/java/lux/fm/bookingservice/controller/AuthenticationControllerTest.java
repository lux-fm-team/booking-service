package lux.fm.bookingservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lux.fm.bookingservice.AbstractMySqlAwareTest;
import lux.fm.bookingservice.dto.exception.ErrorResponseWrapper;
import lux.fm.bookingservice.dto.user.UserLoginRequestDto;
import lux.fm.bookingservice.dto.user.UserLoginResponseDto;
import lux.fm.bookingservice.dto.user.UserRegistrationRequestDto;
import lux.fm.bookingservice.dto.user.UserResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Sql(scripts = "classpath:database/users/delete-user.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
public class AuthenticationControllerTest extends AbstractMySqlAwareTest {
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
    @DisplayName("Register a user")
    void registerUser_ValidRequestDto_Success() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                "test@check.com",
                "testFirstName",
                "testLastName",
                "123456",
                "123456"
        );

        MvcResult mvcResult = mockMvc.perform(post("/api/auth/register")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto responseDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                UserResponseDto.class
        );
        assertThat(responseDto).isNotNull()
                .hasFieldOrPropertyWithValue("email", "test@check.com")
                .hasFieldOrPropertyWithValue("firstName", "testFirstName")
                .hasFieldOrPropertyWithValue("lastName", "testLastName");
    }

    @Sql(scripts = "classpath:database/users/add-user.sql")
    @Test
    @DisplayName("Register a user with short password")
    void registerUser_existingEmail_Conflict() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                "test@check.com",
                "testFirstName",
                "testLastName",
                "123456",
                "123456"
        );

        mockMvc.perform(post("/api/auth/register")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.details").value("Email already exists"));
    }

    @Sql(scripts = "classpath:database/users/add-user.sql")
    @Test
    @DisplayName("Login user valid data")
    void loginUser_ValidRequestDto_Success() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto(
                "test@check.com",
                "123456"
        );

        MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserLoginResponseDto responseDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                UserLoginResponseDto.class
        );
        assertThat(responseDto.token()).isNotNull();
    }

    @Sql(scripts = "classpath:database/users/add-user.sql")
    @Test
    @DisplayName("Incorrect email")
    void loginUser_IncorrectEmail_BadRequest() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto(
                "incorrect@check.com",
                "123456"
        );
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        ErrorResponseWrapper error = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );
        assertThat(error.details()).isEqualTo("Bad credentials");
    }

    @Sql(scripts = "classpath:database/users/add-user.sql")
    @Test
    @DisplayName("Login with incorrect password")
    void loginUser_IncorrectPassword_BadRequest() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto(
                "incorrect@check.com",
                "1234567"
        );
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        ErrorResponseWrapper error = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ErrorResponseWrapper.class
        );
        assertThat(error.details()).isEqualTo("Bad credentials");
    }
}
