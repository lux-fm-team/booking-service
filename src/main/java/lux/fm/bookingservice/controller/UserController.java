package lux.fm.bookingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.user.UpdateUserRequestDto;
import lux.fm.bookingservice.dto.user.UpdateUserRolesRequestDto;
import lux.fm.bookingservice.dto.user.UserResponseDto;
import lux.fm.bookingservice.dto.user.UserResponseDtoWithRoles;
import lux.fm.bookingservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Get your profile info",
            description = "Retrieves general information about your profile."
    )
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('CUSTOMER')")
    public UserResponseDto getMyProfile() {
        return userService.getCurrentUserProfile();
    }

    @Operation(
            summary = "Update your profile info",
            description = "Updates general information about your profile."
    )
    @RequestMapping(value = "/me", method = {RequestMethod.PUT, RequestMethod.PATCH})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('CUSTOMER')")
    public UserResponseDto updateMyProfile(@RequestBody @Valid UpdateUserRequestDto requestDto) {
        return userService.updateCurrentUserProfile(requestDto);
    }

    @Operation(
            summary = "Update user roles",
            description = "Updates certain user's roles by user id."
    )
    @PutMapping("/{id}/role")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDtoWithRoles updateUserRoles(
            @PathVariable @Positive Long id,
            @RequestBody @Valid UpdateUserRolesRequestDto requestDto
    ) {
        return userService.updateUserRoles(id, requestDto);
    }
}
