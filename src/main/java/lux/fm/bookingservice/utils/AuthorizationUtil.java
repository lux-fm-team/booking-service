package lux.fm.bookingservice.utils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.model.User;
import lux.fm.bookingservice.repository.user.UserRepository;
import lux.fm.bookingservice.security.JwtUtil;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorizationUtil {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public User getUserByToken(String token) {
        return userRepository.findByEmail(jwtUtil.getUsername(token)).orElseThrow(
                () -> new EntityNotFoundException("Invalid token")
        );
    }
}
