package com.PCClub.Auth_Service.controllers;

import com.PCClub.Auth_Service.dto.*;
import com.PCClub.Auth_Service.enums.Role;
import com.PCClub.Auth_Service.services.AuthService;
import com.PCClub.Auth_Service.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody RegisterRequest request
    ){
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        AuthenticationResponse authenticationResponse = authService.authenticate(request);
        if (authenticationResponse.getMessage() == null) {
            return ResponseEntity.ok(authenticationResponse);
        } else {
            // Возвращаем статус 401 Unauthorized, если логин или пароль неверные
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authenticationResponse);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String jwt = authHeader.substring(7);

        try {
            String email = jwtService.extractUsername(jwt);
            List<String> roles = jwtService.extractClaims(jwt, claims ->
                    claims.get("role", List.class)
            );

            if (email == null || roles == null || roles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Извлекаем первую роль (если у тебя одна на пользователя)
            String roleStr = roles.get(0);
            Role role = Role.valueOf(roleStr);

            UserDTO dto = UserDTO.builder()
                    .email(email)
                    .role(role)
                    .build();

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
