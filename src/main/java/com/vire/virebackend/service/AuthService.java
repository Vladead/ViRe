package com.vire.virebackend.service;

import com.vire.virebackend.dto.auth.LoginRequest;
import com.vire.virebackend.dto.auth.LoginResponse;
import com.vire.virebackend.dto.auth.RegisterRequest;
import com.vire.virebackend.dto.auth.RegisterResponse;
import com.vire.virebackend.entity.User;
import com.vire.virebackend.repository.UserRepository;
import com.vire.virebackend.security.CustomUserDetails;
import com.vire.virebackend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public RegisterResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);

        var token = jwtService.generateToken(user);
        return new RegisterResponse(user.getId(), token);
    }

    public LoginResponse login(LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        var user = userDetails.getUser();

        var jwt = jwtService.generateToken(user);

        return new LoginResponse(user.getId(), jwt);
    }
}
