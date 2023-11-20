package lux.fm.bookingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.user.UserLoginRequestDto;
import lux.fm.bookingservice.dto.user.UserLoginResponseDto;
import lux.fm.bookingservice.dto.user.UserRegistrationRequestDto;
import lux.fm.bookingservice.dto.user.UserResponseDto;
import lux.fm.bookingservice.security.AuthenticationService;
import lux.fm.bookingservice.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication management",
        description = "Endpoints for authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Login to the system",
            description = """
                    Login to the system using email and password.
                     In response, the JWT token is returned"""
    )
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

    @Operation(
            summary = "Register new user",
            description = """
                    Register new user to the system using email, firstName,
                     lastName and passwords"""
    )
    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto request) {
        return userService.register(request);
    }
}
