package lux.fm.bookingservice.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.user.UserRegistrationRequestDto;
import lux.fm.bookingservice.dto.user.UserResponseDto;
import lux.fm.bookingservice.exception.RegistrationException;
import lux.fm.bookingservice.mapper.UserMapper;
import lux.fm.bookingservice.model.RoleName;
import lux.fm.bookingservice.model.User;
import lux.fm.bookingservice.repository.role.RoleRepository;
import lux.fm.bookingservice.repository.user.UserRepository;
import lux.fm.bookingservice.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(
            UserRegistrationRequestDto request
    ) throws RegistrationException {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RegistrationException("Email already exists");
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(new HashSet<>(Arrays.asList(
                roleRepository.findRoleByName(RoleName.ROLE_CUSTOMER)
        )));
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
