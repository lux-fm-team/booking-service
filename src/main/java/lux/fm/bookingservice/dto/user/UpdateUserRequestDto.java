package lux.fm.bookingservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDto(
        @Email
        String email,
        @Size(max = 50)
        String firstName,
        @Size(max = 50)
        String lastName
) {}
