package com.vire.virebackend.problem;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class ProblemTypeResolver {

    private final ProblemProperties problemProperties;

    public URI uri(ProblemType type) {
        return problemProperties.getBaseUri().resolve(type.slug());
    }
}
