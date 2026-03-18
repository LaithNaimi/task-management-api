package com.laith.taskmanagement.service;

import com.laith.taskmanagement.dto.AuthResponseDTO;
import com.laith.taskmanagement.dto.LoginRequestDTO;
import com.laith.taskmanagement.dto.RegisterRequestDTO;
import com.laith.taskmanagement.exception.EmailAlreadyExistsException;
import com.laith.taskmanagement.exception.InvalidCredentialsException;
import com.laith.taskmanagement.model.AppUser;
import com.laith.taskmanagement.model.Role;
import com.laith.taskmanagement.repository.AppUserRepository;
import com.laith.taskmanagement.security.JwtService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AppUserRepository appUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository appUserRepo, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.appUserRepo = appUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO req) {
        String username = req.getUsername().trim();
        String email = req.getEmail().trim().toLowerCase();

        // Email هو unique
        if (appUserRepo.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.USER);

        AppUser saved;
        try {
            saved = appUserRepo.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException(email);
        }

        var tokenResult = jwtService.generateToken(saved);
        return new AuthResponseDTO(
                tokenResult.token(),
                tokenResult.expiresAt(),
                saved.getId(),
                saved.getRole().name()
        );
    }

    public AuthResponseDTO login(LoginRequestDTO req) {
        String email = req.getEmail().trim().toLowerCase();

        AppUser user = appUserRepo.findByEmailIgnoreCase(email).orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        var tokenResult = jwtService.generateToken(user);
        return new AuthResponseDTO(
                tokenResult.token(),
                tokenResult.expiresAt(),
                user.getId(),
                user.getRole().name()
        );
    }
}
