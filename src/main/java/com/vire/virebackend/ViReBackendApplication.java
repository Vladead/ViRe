package com.vire.virebackend;

import com.vire.virebackend.config.CorsProperties;
import com.vire.virebackend.config.JwtProperties;
import com.vire.virebackend.problem.ProblemProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, ProblemProperties.class, CorsProperties.class})
public class ViReBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ViReBackendApplication.class, args);
    }

}
