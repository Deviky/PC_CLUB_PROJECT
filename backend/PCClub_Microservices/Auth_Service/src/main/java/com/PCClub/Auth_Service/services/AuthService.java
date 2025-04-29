package com.PCClub.Auth_Service.services;


import com.PCClub.Auth_Service.dto.AuthenticationRequest;
import com.PCClub.Auth_Service.dto.AuthenticationResponse;
import com.PCClub.Auth_Service.dto.RegisterRequest;
import com.PCClub.Auth_Service.dto.RegisterResponse;
import com.PCClub.Auth_Service.models.User;
import com.PCClub.Auth_Service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public RegisterResponse register(RegisterRequest request) {
        Optional<User> userDemo = repository.findByEmailIgnoreCase(request.getEmail());
        if (userDemo.isPresent())
            return RegisterResponse.builder()
                    .message("Сотрудник с таким email уже существует")
                    .build();

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        repository.save(user);

        return RegisterResponse.builder()
                .message("Новый сотрудник успешно зарегистрирован!")
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            // Возвращаем сообщение о неверных данных
            return AuthenticationResponse.builder()
                    .message("Неверный логин или пароль!")
                    .build();
        }

        var user = repository.findByEmailIgnoreCase(request.getEmail()).orElse(null);
        if (user == null) {
            // Возвращаем ошибку, если пользователь не найден
            return AuthenticationResponse.builder()
                    .message("Неверный логин или пароль!")
                    .build();
        }

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole())
                .build();
    }

}
