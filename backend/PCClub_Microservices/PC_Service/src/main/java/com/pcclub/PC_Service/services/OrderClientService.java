package com.pcclub.PC_Service.services;

import com.pcclub.PC_Service.dto.OrderWithClientDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderClientService {

    private final WebClient.Builder webClientBuilder;

    public List<OrderWithClientDto> getOrdersByPC(Long PCId) {
        try {
            WebClient webClient = webClientBuilder.baseUrl("http://ORDER-SERVICE").build(); // Используем WebClient с балансировкой нагрузки

            return webClient.get()
                    .uri("/order/get-by-pc/" + PCId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<OrderWithClientDto>>() {}) // Используем ParameterizedTypeReference для List
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении заказов: " + e.getMessage());
        }
    }
}
