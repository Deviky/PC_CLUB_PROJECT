package com.multiplayerserver.gateway.service;

import com.multiplayerserver.gateway.dto.UserDTO;
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
public class AuthServiceClient {

    private final RestTemplate restTemplate;

    public AuthServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserDTO getUserFromToken(String token) {
        try {
            String url = "http://localhost:8095/auth/me";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<UserDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    UserDTO.class
            );

            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("Ошибка: " + e.getStatusCode());
            throw new RuntimeException("Ошибка при получении пользователя", e);
        }
    }
}




