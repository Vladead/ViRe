package com.vire.virebackend.controller.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vire.virebackend.controller.AuthController;
import com.vire.virebackend.controller.UserController;
import com.vire.virebackend.problem.ProblemFactory;
import com.vire.virebackend.problem.ProblemProperties;
import com.vire.virebackend.problem.ProblemTypeResolver;
import com.vire.virebackend.repository.UserRepository;
import com.vire.virebackend.security.JwtAuthenticationFilter;
import com.vire.virebackend.security.JwtService;
import com.vire.virebackend.security.SecurityConfig;
import com.vire.virebackend.security.handler.SecurityProblemHandlers;
import com.vire.virebackend.service.AuthService;
import com.vire.virebackend.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {
        AuthController.class,
        UserController.class
})
@Import({
        GlobalExceptionHandler.class,
        SecurityProblemHandlers.class,
        SecurityConfig.class,
        ProblemFactory.class,
        ProblemTypeResolver.class,
        ProblemDetailWebTest.TestErrorConfig.class,
        ProblemDetailWebTest.Mocks.class
})
class ProblemDetailWebTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @Test
    void register_invalidPayload_returns400_problemDetail() throws Exception {
        var body = Map.of("email", "not-an-email", "password", "123");

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", Matchers.containsString("application/problem+json")))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.type").value("https://vire.dev/problems/validation-error"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void me_withoutToken_returns401_problemDetail() throws Exception {
        mvc.perform(get("/api/user/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("Content-Type", Matchers.containsString("application/problem+json")))
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @TestConfiguration
    @EnableConfigurationProperties(ProblemProperties.class)
    static class TestErrorConfig {
        @Bean
        ProblemProperties problemProperties() {
            var p = new ProblemProperties();
            p.setBaseUri(URI.create("https://vire.dev/problems/"));
            return p;
        }
    }

    @TestConfiguration
    static class Mocks {
        @Bean
        AuthService authService() {
            return Mockito.mock(AuthService.class);
        }

        @Bean
        UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        com.vire.virebackend.security.JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }

        @Bean
        com.vire.virebackend.repository.UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }

        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter(
                JwtService jwtService,
                UserRepository userRepository
        ) {
            return new JwtAuthenticationFilter(jwtService, userRepository);
        }
    }
}
