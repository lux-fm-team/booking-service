package lux.fm.bookingservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Collections;
import lombok.SneakyThrows;
import lux.fm.bookingservice.AbstractPostgresAwareTest;
import lux.fm.bookingservice.dto.user.UpdateUserRequestDto;
import lux.fm.bookingservice.dto.user.UpdateUserRolesRequestDto;
import lux.fm.bookingservice.dto.user.UserResponseDto;
import lux.fm.bookingservice.dto.user.UserResponseDtoWithRoles;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/users/add-user.sql")
@Sql(scripts = {
        "classpath:database/users/delete-user.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
public class UserControllerTest extends AbstractPostgresAwareTest {
    protected static MockMvc mockMvc;
    private static ObjectMapper objectMapper;

    @BeforeAll
    @SneakyThrows
    static void setUp(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Get current user profile")
    @WithUserDetails("test@check.com")
    void getMyProfileEndpointTest_validRequest_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto responseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserResponseDto.class
        );

        assertThat(responseDto).isNotNull()
                .hasFieldOrPropertyWithValue("email", "test@check.com")
                .hasFieldOrPropertyWithValue("firstName", "testFirstName")
                .hasFieldOrPropertyWithValue("lastName", "testLastName");
    }

    @Test
    @DisplayName("Get current user profile - Not Customer")
    @WithMockUser
    void getMyProfileEndpointTest_notCustomer_Forbidden() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Update current user profile")
    @WithUserDetails("test@check.com")
    void updateMyProfileTest_validRequest_Success() throws Exception {
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto(
                null,
                "Nam",
                "Dongyun"
        );

        MvcResult result = mockMvc.perform(put("/api/users/me")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto responseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserResponseDto.class
        );

        assertThat(responseDto).isNotNull()
                .hasFieldOrPropertyWithValue("email", "test@check.com")
                .hasFieldOrPropertyWithValue("firstName", "Nam")
                .hasFieldOrPropertyWithValue("lastName", "Dongyun");
    }

    @Test
    @DisplayName("Update current user profile - Not Customer")
    @WithMockUser
    void updateMyProfileTest_notCustomer_Forbidden() throws Exception {
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto(
                null,
                "John",
                "Doe"
        );

        mockMvc.perform(put("/api/users/me")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Update current user profile - Invalid Request")
    @WithUserDetails("test@check.com")
    void updateMyProfileTest_invalidRequest_BadGateway() throws Exception {
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto(
                "null",
                null,
                null
        );

        mockMvc.perform(put("/api/users/me")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadGateway())
                .andReturn();
    }

    @Test
    @DisplayName("Update user roles by id")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUserRolesTest_validRequest_Success() throws Exception {
        Long userId = 1L;
        UpdateUserRolesRequestDto requestDto = new UpdateUserRolesRequestDto();
        requestDto.setRoleIds(Collections.singleton(1L));

        MvcResult result = mockMvc.perform(put("/api/users/{id}/role", userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDtoWithRoles responseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserResponseDtoWithRoles.class
        );

        assertThat(responseDto).isNotNull()
                .hasFieldOrPropertyWithValue("email", "test@check.com")
                .hasFieldOrPropertyWithValue("firstName", "testFirstName")
                .hasFieldOrPropertyWithValue("lastName", "testLastName")
                .hasFieldOrPropertyWithValue("roleIds", Collections.singleton(1L));
    }

    @Test
    @DisplayName("Update user roles by id - Not Admin")
    @WithMockUser
    void updateUserRolesTest_notAdmin_Forbidden() throws Exception {
        Long userId = 1L;
        UpdateUserRolesRequestDto requestDto = new UpdateUserRolesRequestDto();
        requestDto.setRoleIds(Collections.singleton(1L));

        mockMvc.perform(put("/api/users/{id}/role", userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Update user roles by id - Invalid ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUserRolesTest_invalidID_NotFound() throws Exception {
        Long invalidUserId = 2L;
        UpdateUserRolesRequestDto requestDto = new UpdateUserRolesRequestDto();
        requestDto.setRoleIds(Collections.singleton(1L));

        mockMvc.perform(put("/api/users/{id}/role", invalidUserId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update user roles by id - Invalid DTO")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUserRolesTest_invalidDto_BadGateway() throws Exception {
        Long userId = 1L;
        UpdateUserRolesRequestDto requestDto = new UpdateUserRolesRequestDto();
        requestDto.setRoleIds(Collections.emptySet());

        mockMvc.perform(put("/api/users/{id}/role", userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadGateway());
    }
}
