package com.vire.virebackend.service;

import com.vire.virebackend.dto.auth.RegisterRequest;
import com.vire.virebackend.dto.auth.RegisterResponse;
import com.vire.virebackend.entity.User;
import com.vire.virebackend.repository.UserRepository;
import com.vire.virebackend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);

        var token = jwtService.generateToken(user);
        return new RegisterResponse(token);
    }
}
