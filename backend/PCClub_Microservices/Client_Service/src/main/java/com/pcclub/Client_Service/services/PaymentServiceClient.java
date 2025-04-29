package com.pcclub.Client_Service.services;

import com.pcclub.Client_Service.dto.Wallet;
import com.pcclub.Client_Service.dto.WalletCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PaymentServiceClient {
    @Autowired
    private WebClient webClient;

    public void addClientWallet(WalletCreateRequest wallet) {
        webClient.post()
                .uri("http://localhost:8092/payment/create-wallet")
                .body(Mono.just(wallet), WalletCreateRequest.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public Wallet getWallet(Long clientId) {
        return webClient.get()
                .uri("http://localhost:8092/payment/wallet/" + clientId)
                .retrieve()
                .bodyToMono(Wallet.class)
                .block();
    }

}
