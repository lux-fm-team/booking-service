package lux.fm.bookingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.user.UpdateUserRequestDto;
import lux.fm.bookingservice.dto.user.UpdateUserRolesRequestDto;
import lux.fm.bookingservice.dto.user.UserRegistrationRequestDto;
import lux.fm.bookingservice.dto.user.UserResponseDto;
import lux.fm.bookingservice.dto.user.UserResponseDtoWithRoles;
import lux.fm.bookingservice.exception.RegistrationException;
import lux.fm.bookingservice.mapper.UserMapper;
import lux.fm.bookingservice.model.Role;
import lux.fm.bookingservice.model.User;
import lux.fm.bookingservice.repository.RoleRepository;
import lux.fm.bookingservice.repository.UserRepository;
import lux.fm.bookingservice.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        if (userRepository.existsByEmail(request.email())) {
            throw new RegistrationException("Email already exists");
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(new HashSet<>(Arrays.asList(
                roleRepository.findRoleByName(Role.RoleName.ROLE_CUSTOMER)
        )));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto getCurrentUserProfile() {
        return userMapper.toDto(getCurrentlyAuthenticatedUser());
    }

    @Override
    public UserResponseDto updateCurrentUserProfile(UpdateUserRequestDto requestDto) {
        User user = getCurrentlyAuthenticatedUser();
        userMapper.mapUpdateRequestToUser(requestDto, user);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDtoWithRoles updateUserRoles(Long id, UpdateUserRolesRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("""
                             Update roles operation failed.
                             User with id %d doesn't exist.""".formatted(id)));
        addUserRoles(requestDto.getRoleIds(), user);
        return userMapper.toDtoWithRoles(userRepository.save(user));
    }

    private User getCurrentlyAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .get();
    }

    private void addUserRoles(Set<Long> roleIds, User user) {
        Set<Role> roles = new HashSet<>(
                roleRepository.findAllById(roleIds));
        user.setRoles(roles);
    }
}
