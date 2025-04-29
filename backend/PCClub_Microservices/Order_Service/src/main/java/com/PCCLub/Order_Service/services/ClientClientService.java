package com.PCCLub.Order_Service.services;

import com.PCCLub.Order_Service.dto.ClientDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientClientService {

    private final WebClient webClient;

    public ClientDto getClientById(Long clientId) {
        try {
            return webClient.get()
                    .uri("http://localhost:8093/client/get/" + clientId)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .map(msg -> new RuntimeException("Ошибка при получении клиента: " + msg))
                    )
                    .bodyToMono(ClientDto.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении клиента: " + e.getMessage());
        }
    }

    public List<ClientDto> getAllClients() {
        try {
            return webClient.get()
                    .uri("http://localhost:8093/client/get-all")  // Новый эндпоинт для получения всех клиентов
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .map(msg -> new RuntimeException("Ошибка при получении клиентов: " + msg))
                    )
                    .bodyToFlux(ClientDto.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении клиентов: " + e.getMessage());
        }
    }
}
