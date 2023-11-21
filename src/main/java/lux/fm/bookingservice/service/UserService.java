package lux.fm.bookingservice.service;

import lux.fm.bookingservice.dto.user.UpdateUserRequestDto;
import lux.fm.bookingservice.dto.user.UpdateUserRolesRequestDto;
import lux.fm.bookingservice.dto.user.UserRegistrationRequestDto;
import lux.fm.bookingservice.dto.user.UserResponseDto;
import lux.fm.bookingservice.dto.user.UserResponseDtoWithRoles;
import lux.fm.bookingservice.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;

    UserResponseDto getCurrentUserProfile();

    UserResponseDto updateCurrentUserProfile(UpdateUserRequestDto requestDto);

    UserResponseDtoWithRoles updateUserRoles(Long id, UpdateUserRolesRequestDto requestDto);
}
