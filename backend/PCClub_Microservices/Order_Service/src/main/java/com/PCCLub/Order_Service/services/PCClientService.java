package com.PCCLub.Order_Service.services;

import com.PCCLub.Order_Service.dto.ReserveDto;
import com.PCCLub.Order_Service.dto.ServiceDictDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PCClientService {

    private final WebClient webClient;

    public String reservePC(ReserveDto reserveDto) {
        try {
            return webClient.post()
                    .uri("http://localhost:8091/pc-service/pc/reserve") // адрес сервиса
                    .body(Mono.just(reserveDto), ReserveDto.class)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                            response.bodyToMono(String.class)
                                    .map(msg -> new RuntimeException("Ошибка: " + msg)))
                    .bodyToMono(String.class)
                    .block(); // блокируем, если не используем reactive
        } catch (Exception e) {
            return "Ошибка при бронировании: " + e.getMessage();
        }
    }

    public String cancelReservation(ReserveDto reserveDto) {
        try {
            return webClient.post()
                    .uri("http://localhost:8091/pc-service/pc/cancel-reserve")
                    .body(Mono.just(reserveDto), ReserveDto.class)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                            response.bodyToMono(String.class)
                                    .map(msg -> new RuntimeException("Ошибка при отмене бронирования: " + msg))
                    )
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            return "Ошибка при отмене бронирования: " + e.getMessage();
        }
    }

    public ServiceDictDto getService(Long serviceId) {
        try {
            return webClient.get()
                    .uri("http://localhost:8091/pc-service/service/get/" +  serviceId)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                            response.bodyToMono(String.class)
                                    .map(msg -> new RuntimeException("Ошибка: " + msg)))
                    .bodyToMono(ServiceDictDto.class)
                    .block();  // блокируем, если не используем reactive
        } catch (Exception e) {
            // Можно вернуть null или бросить исключение, если не удалось получить данные
            return null;
        }
    }
}
