package com.laith.taskmanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laith.taskmanagement.exception.ProblemTypes;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain  securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthConverter, ObjectMapper objectMapper) throws Exception{

      return http.csrf(AbstractHttpConfigurer::disable)
              .cors(Customizer.withDefaults())
              .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .authorizeHttpRequests(auth -> auth
                      //Swagger
                      .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                      //Auth endpoints
                      .requestMatchers("/api/auth/**").permitAll()
                      //Tasks
                      .requestMatchers("/api/tasks/**").authenticated()
                      // Categories
                      .requestMatchers("/api/categories/**").authenticated()
                      .anyRequest().authenticated())
              .exceptionHandling(ex -> ex
                      .authenticationEntryPoint(authenticationEntryPoint(objectMapper))
                      .accessDeniedHandler(accessDeniedHandler(objectMapper))
              )
              .oauth2ResourceServer(oauth2 -> oauth2
                      .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
              )
              .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        // Convert claim "role" -> authority ROLE_USER / ROLE_ADMIN
        JwtAuthenticationConverter rolesConverter = new JwtAuthenticationConverter();
        rolesConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");
            if (role == null || role.isBlank()) return List.of();
            return List.of(new SimpleGrantedAuthority("ROLE_" + role));
        });
        return rolesConverter;
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {
        byte[] secretBytes = secretBytes(secret);
        SecretKey key = new SecretKeySpec(secretBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    JwtEncoder jwtEncoder(@Value("${jwt.secret}") String secret) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) throw new IllegalArgumentException("jwt.secret must be at least 32 bytes for HS256");

        SecretKey key = new SecretKeySpec(bytes, "HmacSHA256");
        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }


    private byte[] secretBytes(String secret) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalArgumentException("jwt.secret must be at least 32 bytes for HS256");
        }
        return bytes;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
        return (request, response, authException) -> {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNAUTHORIZED,
                    "Missing or invalid authentication token"
            );
            pd.setTitle("Unauthorized");
            pd.setType(ProblemTypes.UNAUTHORIZED);
            pd.setInstance(URI.create(request.getRequestURI()));
            pd.setProperty("timestamp", Instant.now().toString());

            writeProblem(response, objectMapper, pd);
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {
        return (request, response, accessDeniedException) -> {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                    HttpStatus.FORBIDDEN,
                    "You do not have permission to access this resource"
            );
            pd.setTitle("Forbidden");
            pd.setType(ProblemTypes.FORBIDDEN);
            pd.setInstance(URI.create(request.getRequestURI()));
            pd.setProperty("timestamp", Instant.now().toString());

            writeProblem(response, objectMapper, pd);
        };
    }

    private void writeProblem(HttpServletResponse response, ObjectMapper objectMapper, ProblemDetail pd) throws java.io.IOException {
        response.setStatus(pd.getStatus());
        response.setContentType("application/problem+json");
        objectMapper.writeValue(response.getOutputStream(), pd);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:4200"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of(
                "Authorization", "Content-Type"
        ));

        config.setExposedHeaders(List.of(
                "Authorization"
        ));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
