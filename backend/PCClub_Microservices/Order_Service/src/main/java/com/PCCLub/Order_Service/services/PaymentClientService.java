package com.PCCLub.Order_Service.services;

import com.PCCLub.Order_Service.dto.PaymentCancelResponse;
import com.PCCLub.Order_Service.dto.PaymentRequest;
import com.PCCLub.Order_Service.dto.PaymentResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentClientService {

    private final WebClient.Builder webClientBuilder;

    private WebClient getWebClient() {
        return webClientBuilder.baseUrl("http://PAYMENT-SERVICE").build();
    }

    public PaymentResponse sendPayment(PaymentRequest paymentRequest) {
        try {
            return getWebClient().post()
                    .uri("/payment/make-payment")
                    .body(Mono.just(paymentRequest), PaymentRequest.class)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .map(msg -> new RuntimeException("Ошибка оплаты: " + msg))
                    )
                    .bodyToMono(PaymentResponse.class)
                    .block();
        } catch (Exception e) {
            return new PaymentResponse(
                    paymentRequest.getClientId(),
                    paymentRequest.getServiceOrderId(),
                    "Ошибка при оплате: " + e.getMessage()
            );
        }
    }

    public PaymentCancelResponse cancelOperation(Long orderId) {
        try {
            return getWebClient().post()
                    .uri("/payment/cancel-payment")
                    .body(Mono.just(orderId), Long.class)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .map(msg -> new RuntimeException("Ошибка отмены оплаты: " + msg))
                    )
                    .bodyToMono(PaymentCancelResponse.class)
                    .block();
        } catch (Exception e) {
            return new PaymentCancelResponse(
                    "Ошибка при отмене оплаты: " + e.getMessage()
            );
        }
    }
}

