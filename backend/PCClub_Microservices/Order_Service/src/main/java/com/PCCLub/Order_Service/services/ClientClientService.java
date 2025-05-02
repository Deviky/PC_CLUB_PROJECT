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

    private final WebClient.Builder webClientBuilder;

    private WebClient getWebClient() {
        return webClientBuilder.baseUrl("http://CLIENT-SERVICE").build();
    }

    public ClientDto getClientById(Long clientId) {
        try {
            return getWebClient().get()
                    .uri("/client/get/" + clientId)
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
            return getWebClient().get()
                    .uri("/client/get-all")
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

