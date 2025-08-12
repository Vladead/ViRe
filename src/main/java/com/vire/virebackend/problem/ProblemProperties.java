package com.vire.virebackend.problem;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@Getter
@Setter
@ConfigurationProperties(prefix = "problem")
public class ProblemProperties {

    /**
     * Base URI for ProblemDetail.type (must end with /).
     * Only used if not set in application.yml
     */
    private URI baseUri = URI.create("https://vire.dev/problems/");
}
