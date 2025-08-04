package com.vire.virebackend.security;

import com.vire.virebackend.entity.User;
import lombok.Getter;

import java.util.List;

@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {

    private final User user;

    public CustomUserDetails(User user) {
        super(
                user.getEmail(),
                user.getPassword(),
                user.getIsActive(),
                true,
                true,
                true,
                List.of() // there is no roles for now
        );
        this.user = user;
    }
}
