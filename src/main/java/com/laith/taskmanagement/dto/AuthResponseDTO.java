package com.laith.taskmanagement.dto;

import lombok.Getter;

import java.time.Instant;
@Getter
public class AuthResponseDTO {
    private String token;
    private String tokenType = "Bearer";
    private Instant expiresAt;
    private Long userId;
    private String role;

    public AuthResponseDTO(String token, Instant expiresAt, Long userId, String role) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.userId = userId;
        this.role = role;
    }
}
