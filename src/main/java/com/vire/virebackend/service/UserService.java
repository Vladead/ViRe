package com.vire.virebackend.service;

import com.vire.virebackend.dto.user.UserDto;
import com.vire.virebackend.mapper.UserMapper;
import com.vire.virebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }
}
