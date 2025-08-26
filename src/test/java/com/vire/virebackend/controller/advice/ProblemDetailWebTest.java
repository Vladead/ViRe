package com.vire.virebackend.controller.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vire.virebackend.config.CorsProperties;
import com.vire.virebackend.controller.AuthController;
import com.vire.virebackend.controller.UserController;
import com.vire.virebackend.entity.User;
import com.vire.virebackend.problem.ProblemFactory;
import com.vire.virebackend.problem.ProblemProperties;
import com.vire.virebackend.problem.ProblemTypeResolver;
import com.vire.virebackend.repository.UserRepository;
import com.vire.virebackend.security.JwtService;
import com.vire.virebackend.security.SecurityConfig;
import com.vire.virebackend.security.filter.JwtAuthenticationFilter;
import com.vire.virebackend.security.filter.MdcLoggingFilter;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {
        AuthController.class,
        UserController.class,
        ProblemDetailWebTest.ErrorThrowingController.class,
        ProblemDetailWebTest.AdminOnlyController.class
})
@Import({
        GlobalExceptionHandler.class,
        FallbackExceptionHandler.class,
        SecurityProblemHandlers.class,
        SecurityConfig.class,
        ProblemFactory.class,
        ProblemTypeResolver.class,
        ProblemDetailWebTest.TestErrorConfig.class,
        ProblemDetailWebTest.Mocks.class,
        ProblemDetailWebTest.MethodSecurityTestConfig.class,
        ProblemDetailWebTest.ErrorThrowingController.class,
        ProblemDetailWebTest.AdminOnlyController.class
})
@TestPropertySource(properties = {
        "jwt.secret=aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
        "jwt.expiration=1h",
        "spring.mvc.problemdetails.enabled=true"
})
class ProblemDetailWebTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @Autowired
    AuthService authService;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRepository userRepository;

    @Test
    void register_invalidPayload_returns400_problemDetail() throws Exception {
        var body = Map.of(
                "username", "",
                "email", "not-an-email",
                "password", "123"
        );

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
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.type").value("https://vire.dev/problems/authentication"));
    }

    @Test
    void login_wrongPassword_returns401_problemDetail() throws Exception {
        var body = Map.of(
                "email", "a@b.c",
                "password", "wrongpass"
        );

        Mockito.when(authService.login(Mockito.any()))
                .thenThrow(new BadCredentialsException("Bad creds"));

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("Content-Type", Matchers.containsString("application/problem+json")))
                .andExpect(header().string("WWW-Authenticate", Matchers.containsString("Bearer realm=\"ViRe\"")))
                .andExpect(jsonPath("$.type").value("https://vire.dev/problems/authentication"));
    }

    @Test
    void register_duplicateEmail_returns409_problemDetail() throws Exception {
        var body = Map.of(
                "username", "user1",
                "email", "a@b.c",
                "password", "12345678"
        );

        Mockito.when(authService.register(Mockito.any()))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(header().string("Content-Type", Matchers.containsString("application/problem+json")));
    }

    @Test
    void me_withValidToken_returns200() throws Exception {
        var userId = UUID.randomUUID();
        var token = "valid_jwt_token";

        Mockito.when(jwtService.extractUserId(token)).thenReturn(userId);

        var user = User.builder()
                .username("john")
                .email("john@example.com")
                .password("$2a$10$hash")
                .build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(jwtService.isTokenValid(token, user)).thenReturn(true);

        mvc.perform(get("/api/user/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.roles").isArray());
    }

    @Test
    void notFound_nonExistingEndpoint_returns404_problemDetail() throws Exception {
        mvc.perform(get("/api/does-not-exist").with(user("john")))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", Matchers.containsString("application/problem+json")))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void forbidden_insufficientRole_returns403_problemDetail() throws Exception {
        mvc.perform(get("/api/test/admin-only").with(user("john").roles("USER")))
                .andExpect(status().isForbidden())
                .andExpect(header().string("Content-Type", Matchers.containsString("application/problem+json")))
                .andExpect(jsonPath("$.title").value("Forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.type").value("https://vire.dev/problems/forbidden"));
    }

    @Test
    void internalServerError_runtimeException_returns500_problemDetail_withIncidentId() throws Exception {
        mvc.perform(get("/api/test/error").with(user("john")))
                .andExpect(status().isInternalServerError())
                .andExpect(header().string("Content-Type", Matchers.containsString("application/problem+json")))
                .andExpect(jsonPath("$.title").value("Internal server error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.type").value("https://vire.dev/problems/internal"))
                .andExpect(jsonPath("$.incidentId").exists());
    }

    @Test
    void register_malformedJson_returns400_problemDetail() throws Exception {
        var malformedJson = "{"; // invalid JSON

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", Matchers.containsString("application/problem+json")))
                .andExpect(jsonPath("$.title").value("Bad request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.type").value("https://vire.dev/problems/bad-request"));
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

        @Bean
        MdcLoggingFilter mdcLoggingFilter() {
            return new MdcLoggingFilter();
        }

        @Bean
        CorsProperties corsProperties() {
            var p = new CorsProperties();
            p.setAllowedOrigins(java.util.List.of("http://localhost:5173"));
            return p;
        }
    }

    @TestConfiguration
    @EnableMethodSecurity
    public static class MethodSecurityTestConfig {
    }

    @RestController
    @RequestMapping("/api/test")
    public static class ErrorThrowingController {
        @GetMapping("/error")
        public ResponseEntity<Void> error() {
            throw new RuntimeException("boom");
        }
    }

    @RestController
    @RequestMapping("/api/test")
    public static class AdminOnlyController {
        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/admin-only")
        public ResponseEntity<String> adminOnly() {
            return ResponseEntity.ok("ok");
        }
    }
}
