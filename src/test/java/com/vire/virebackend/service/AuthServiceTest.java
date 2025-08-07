package com.vire.virebackend.service;

import com.vire.virebackend.dto.auth.RegisterRequest;
import com.vire.virebackend.entity.User;
import com.vire.virebackend.repository.UserRepository;
import com.vire.virebackend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest("bob_bobson", "bobb@example.com", "plainPassword");
    }

    @Test
    void register_shouldSaveUserAndReturnToken() {
        var encoded = "EncodedPassword";
        when(passwordEncoder.encode(request.password())).thenReturn(encoded);

        // userRepository.save() should return object with generated id
        var savedUser = User.builder()
                .username(request.username())
                .email(request.email())
                .password(encoded)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        var expectedToken = "jwt-token";
        when(jwtService.generateToken(any(User.class))).thenReturn(expectedToken);

        // when
        var response = authService.register(request);

        // then
        // check saving user with encoded password
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        assertThat(captured.getPassword()).isEqualTo(encoded);

        // check token generation
        verify(jwtService).generateToken(captured);

        // check method result
        assertThat(response.id()).isEqualTo(savedUser.getId());
        assertThat(response.token()).isEqualTo(expectedToken);
    }
}
