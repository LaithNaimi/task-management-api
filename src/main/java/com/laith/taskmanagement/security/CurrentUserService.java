package com.laith.taskmanagement.security;

import com.laith.taskmanagement.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Unauthenticated");
        }

        String subject;
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            subject = jwtAuth.getToken().getSubject();
        } else {
            subject = auth.getName();
        }

        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException ex) {
            throw new UnauthorizedException("Invalid token subject");
        }
    }

}
