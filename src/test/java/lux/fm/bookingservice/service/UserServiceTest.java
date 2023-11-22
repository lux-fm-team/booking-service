package lux.fm.bookingservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Set;
import lux.fm.bookingservice.dto.user.UserRegistrationRequestDto;
import lux.fm.bookingservice.dto.user.UserResponseDto;
import lux.fm.bookingservice.mapper.UserMapper;
import lux.fm.bookingservice.mapper.impl.UserMapperImpl;
import lux.fm.bookingservice.model.Role;
import lux.fm.bookingservice.model.User;
import lux.fm.bookingservice.repository.RoleRepository;
import lux.fm.bookingservice.repository.UserRepository;
import lux.fm.bookingservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
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
    void testRegisterUser() {

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
        when(userRepository.save(any())).thenReturn(user);

        UserResponseDto responseDto = userService.register(requestDto);

        assertThat(responseDto)
                .hasFieldOrPropertyWithValue("email", "test@check.com")
                .hasFieldOrPropertyWithValue("firstName", "testFirstName")
                .hasFieldOrPropertyWithValue("lastName", "testLastName");
    }
}
