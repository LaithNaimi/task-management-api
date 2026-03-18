package com.laith.taskmanagement.controller;

import com.laith.taskmanagement.dto.AuthResponseDTO;
import com.laith.taskmanagement.dto.LoginRequestDTO;
import com.laith.taskmanagement.dto.RegisterRequestDTO;
import com.laith.taskmanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO req) {
        AuthResponseDTO res = authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO req) {
        AuthResponseDTO res = authService.login(req);
        return ResponseEntity.ok(res);
    }
}
