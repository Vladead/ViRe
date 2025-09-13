package com.vire.virebackend.service;

import com.vire.virebackend.dto.auth.RegisterRequest;
import com.vire.virebackend.entity.Role;
import com.vire.virebackend.entity.Session;
import com.vire.virebackend.entity.User;
import com.vire.virebackend.repository.RoleRepository;
import com.vire.virebackend.repository.SessionRepository;
import com.vire.virebackend.repository.UserRepository;
import com.vire.virebackend.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest("bob_bobson", "bobb@example.com", "plainPassword");
    }

    @Test
    void register_shouldSaveUser_issueJwtWithJti_andCreateServerSession() {
        // arrange
        var http = mock(HttpServletRequest.class);
        when(http.getHeader("User-Agent")).thenReturn("JUnit/Mockito UA");
        when(http.getRemoteAddr()).thenReturn("127.0.0.1");

        var encoded = "EncodedPassword";
        when(passwordEncoder.encode(request.password())).thenReturn(encoded);

        var role = new Role();
        role.setName("USER");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));

        // userRepository.save returns the same instance (id may be null in unit test)
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // sessionRepository.save returns the same session
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var expectedToken = "jwt-token";
        when(jwtService.generateToken(any(User.class), any(UUID.class))).thenReturn(expectedToken);

        // act
        var response = authService.register(request, http);

        // assert: user saved with encoded password
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        var savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isEqualTo(encoded);
        assertThat(savedUser.getRoles()).extracting("name").contains("USER");

        // assert: session saved with non-null jti and active
        ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(sessionCaptor.capture());
        var savedSession = sessionCaptor.getValue();
        assertThat(savedSession.getUser()).isEqualTo(savedUser);
        assertThat(savedSession.getJti()).isNotNull();
        assertThat(savedSession.getIsActive()).isTrue();
        assertThat(savedSession.getUserAgent()).isEqualTo("JUnit/Mockito UA");
        assertThat(savedSession.getIp()).isEqualTo("127.0.0.1");

        // assert: jwt issued with the same jti
        ArgumentCaptor<UUID> jtiCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(jwtService).generateToken(eq(savedUser), jtiCaptor.capture());
        assertThat(jtiCaptor.getValue()).isEqualTo(savedSession.getJti());

        // assert: response contains token
        assertThat(response.token()).isEqualTo(expectedToken);
    }
}
