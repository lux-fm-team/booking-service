package lux.fm.bookingservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lux.fm.bookingservice.validation.FieldMatch;

@FieldMatch(field = {"password", "repeatPassword"}, message = "Password do not match")
public record UserRegistrationRequestDto(
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Size(max = 50)
        String firstName,
        @NotBlank
        @Size(max = 50)
        String lastName,
        @NotBlank
        @Size(min = 6, max = 60)
        String password,
        @NotBlank
        @Size(min = 6, max = 60)
        String repeatPassword
) {
}
