package lux.fm.bookingservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lux.fm.bookingservice.dto.user.UpdateUserRequestDto;
import lux.fm.bookingservice.dto.user.UpdateUserRolesRequestDto;
import lux.fm.bookingservice.dto.user.UserRegistrationRequestDto;
import lux.fm.bookingservice.dto.user.UserResponseDto;
import lux.fm.bookingservice.dto.user.UserResponseDtoWithRoles;
import lux.fm.bookingservice.mapper.UserMapper;
import lux.fm.bookingservice.mapper.impl.UserMapperImpl;
import lux.fm.bookingservice.model.Role;
import lux.fm.bookingservice.model.User;
import lux.fm.bookingservice.repository.RoleRepository;
import lux.fm.bookingservice.repository.UserRepository;
import lux.fm.bookingservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Spy
    private UserMapper userMapper = new UserMapperImpl();
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Register new user")
    void testRegisterUser_OK() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                "test@check.com",
                "testFirstName",
                "testLastName",
                "123456",
                "123456"
        );
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(false);

        Role role = new Role();
        role.setName(Role.RoleName.valueOf("ROLE_CUSTOMER"));
        User user = userMapper.toUser(requestDto);
        user.setPassword("encoded123456");
        user.setRoles(Set.of(role));
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(false);
        when(roleRepository.findRoleByName(Role.RoleName.ROLE_CUSTOMER)).thenReturn(role);
        when(passwordEncoder.encode(requestDto.password())).thenReturn("encoded123456");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUser(requestDto)).thenReturn(user);

        UserResponseDto responseDto = userService.register(requestDto);

        assertThat(responseDto)
                .hasFieldOrPropertyWithValue("email", "test@check.com")
                .hasFieldOrPropertyWithValue("firstName", "testFirstName")
                .hasFieldOrPropertyWithValue("lastName", "testLastName");
    }

    @Test
    @DisplayName("Get Current User Profile")
    void getProfileTest_OK() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User mockedUser = new User();
        mockedUser.setEmail("test@example.com");
        mockedUser.setFirstName("Nam");
        mockedUser.setLastName("Dongyun");

        UserDetails userDetails = Mockito.mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(mockedUser.getEmail());

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockedUser));

        UserResponseDto resultDto = userService.getCurrentUserProfile();

        assertThat(resultDto)
                .hasFieldOrPropertyWithValue("email", "test@example.com")
                .hasFieldOrPropertyWithValue("firstName", "Nam")
                .hasFieldOrPropertyWithValue("lastName", "Dongyun");
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userMapper, times(1)).toDto(any());
    }

    @Test
    @DisplayName("Update Current User Profile")
    void updateProfileTest_OK() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User mockedUser = new User();
        mockedUser.setId(1L);
        mockedUser.setEmail("test@example.com");
        mockedUser.setFirstName("Nam");
        mockedUser.setLastName("Dongyun");

        UserDetails userDetails = Mockito.mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(mockedUser.getEmail());

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockedUser));
        when(userRepository.save(mockedUser)).thenReturn(mockedUser);

        UpdateUserRequestDto updateDto = new UpdateUserRequestDto(
                null,
                "John",
                "Doe"
        );

        UserResponseDto resultDto = userService.updateCurrentUserProfile(updateDto);

        assertThat(resultDto)
                .hasFieldOrPropertyWithValue("email", "test@example.com")
                .hasFieldOrPropertyWithValue("firstName", "John")
                .hasFieldOrPropertyWithValue("lastName", "Doe");
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).save(any());
        verify(userMapper, times(1)).toDto(any());
    }

    @Test
    @DisplayName("Update User Roles")
    void updateUserRolesTest_OK() {
        UpdateUserRolesRequestDto updateDto = new UpdateUserRolesRequestDto();
        Set<Long> roleIds = new HashSet<>();
        roleIds.add(1L);
        roleIds.add(2L);
        updateDto.setRoleIds(roleIds);

        User mockedUser = new User();
        mockedUser.setId(1L);
        mockedUser.setEmail("test@example.com");
        mockedUser.setFirstName("Nam");
        mockedUser.setLastName("Dongyun");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockedUser));
        when(userRepository.save(mockedUser)).thenReturn(mockedUser);

        Role role1 = new Role();
        role1.setId(1L);
        Role role2 = new Role();
        role2.setId(2L);
        when(roleRepository.findAllById(roleIds)).thenReturn(List.of(role1, role2));

        UserResponseDtoWithRoles result = userService.updateUserRoles(mockedUser.getId(), updateDto);

        assertThat(result.getRoleIds()).contains(1L, 2L);
        verify(userRepository, times(1)).findById(anyLong());
        verify(roleRepository, times(1)).findAllById(roleIds);
        verify(userRepository, times(1)).save(any());
        verify(userMapper, times(1)).toDtoWithRoles(any());
    }

    @Test
    @DisplayName("Update User Roles - User Not Found")
    void updateUserRolesTest_NotFound() {
        Long userId = 1L;
        UpdateUserRolesRequestDto updateDto = new UpdateUserRolesRequestDto();
        Set<Long> roleIds = new HashSet<>();
        roleIds.add(1L);
        roleIds.add(2L);
        updateDto.setRoleIds(roleIds);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserRoles(userId, updateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("""
                             Update roles operation failed.
                             User with id %d doesn't exist.""".formatted(userId));

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(roleRepository, userMapper);
    }
}
