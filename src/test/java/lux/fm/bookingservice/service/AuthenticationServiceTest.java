package lux.fm.bookingservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import lux.fm.bookingservice.dto.user.UserLoginRequestDto;
import lux.fm.bookingservice.dto.user.UserLoginResponseDto;
import lux.fm.bookingservice.security.AuthenticationService;
import lux.fm.bookingservice.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private Authentication authentication;

    @Test
    void testAuthenticate() {
        UserLoginRequestDto requestDto = new UserLoginRequestDto(
                "test@check.com",
                "123456"
        );
        String token = "token";

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getName()).thenReturn(requestDto.email());
        when(jwtUtil.generateToken(requestDto.email())).thenReturn(token);

        UserLoginResponseDto responseDto = authenticationService.authenticate(requestDto);

        assertEquals(token, responseDto.token());
    }
}
