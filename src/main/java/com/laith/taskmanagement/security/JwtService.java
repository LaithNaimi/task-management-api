package com.laith.taskmanagement.security;

import com.laith.taskmanagement.model.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expirationSeconds;
    private final String issuer;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${jwt.expiration-seconds}") long expirationSeconds,
            @Value("${jwt.issuer}") String issuer
    ) {
        this.jwtEncoder = jwtEncoder;
        this.expirationSeconds = expirationSeconds;
        this.issuer = issuer;
    }

    public TokenResult generateToken(AppUser user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationSeconds);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(exp)
                // subject = userId
                .subject(String.valueOf(user.getId()))
                .claim("role", user.getRole().name())
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                .type("JWT")
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header,claims)).getTokenValue();
        return new TokenResult(token, exp);
    }

    public record TokenResult(String token, Instant expiresAt) {}
}
