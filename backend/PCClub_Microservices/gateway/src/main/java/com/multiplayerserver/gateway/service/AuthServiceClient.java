package com.multiplayerserver.gateway.service;

import com.multiplayerserver.gateway.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class AuthServiceClient {

    private final WebClient.Builder webClientBuilder;

    public UserDTO getUserFromToken(String token) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://AUTH-SERVICE/auth/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .flatMap(msg -> Mono.error(new RuntimeException("Ошибка при получении пользователя: " + msg)))
                    )
                    .bodyToMono(UserDTO.class) // Возвращаем UserDTO
                    .block(); // Блокировка для синхронного возврата результата
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении пользователя: " + e.getMessage(), e);
        }
    }
}



