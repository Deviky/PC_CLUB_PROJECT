package com.pcclub.Client_Service.services;

import com.pcclub.Client_Service.dto.Wallet;
import com.pcclub.Client_Service.dto.WalletCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentServiceClient {

    private final WebClient.Builder webClientBuilder;

    private WebClient getWebClient() {
        return webClientBuilder.baseUrl("http://PAYMENT-SERVICE").build();
    }

    public void addClientWallet(WalletCreateRequest wallet) {
        try {
            getWebClient().post()
                    .uri("/payment/create-wallet")
                    .body(Mono.just(wallet), WalletCreateRequest.class)
                    .retrieve()
                    .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                    .map(msg -> new RuntimeException("Ошибка при создании кошелька: " + msg)))
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании кошелька: " + e.getMessage());
        }
    }

    public Wallet getWallet(Long clientId) {
        try {
            return getWebClient().get()
                    .uri("/payment/wallet/" + clientId)
                    .retrieve()
                    .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                    .map(msg -> new RuntimeException("Ошибка при получении кошелька: " + msg)))
                    .bodyToMono(Wallet.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении кошелька: " + e.getMessage());
        }
    }
}