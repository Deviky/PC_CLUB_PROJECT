package com.pcclub.Client_Service.services;

import com.pcclub.Client_Service.dto.ClientCreateRequest;
import com.pcclub.Client_Service.dto.ClientDto;
import com.pcclub.Client_Service.dto.Wallet;
import com.pcclub.Client_Service.dto.WalletCreateRequest;
import com.pcclub.Client_Service.models.Client;
import com.pcclub.Client_Service.repositories.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final PaymentServiceClient paymentServiceClient;

    @Transactional

    public String addClient(ClientCreateRequest client) {
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!Pattern.matches(emailPattern, client.getEmail())) {
            return "Ошибка: email имеет некорректный формат!";
        }

        if (clientRepository.findByEmailIgnoreCase(client.getEmail()).isPresent())
            return "Ошибка: клиент с таким email уже существует!";

        Long id = clientRepository.save(Client.builder()
                .email(client.getEmail())
                .name(client.getName())
                .surname(client.getSurname())
                .dateOfBirth(client.getDateOfBirth())
                .build()).getId();

        try {
            paymentServiceClient.addClientWallet(WalletCreateRequest.builder()
                    .clientId(id)
                    .bonusBalance(200f)
                    .build());
        }
        catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "Ошибка: что-то пошло не так!";
        }
        return "Клиент успешно добавлен";
    }

    public ClientDto getClientByEmail(String email) {
        Client client = clientRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NoSuchElementException("Клиент с email " + email + " не найден"));

        return ClientDto.builder()
                .id(client.getId())
                .name(client.getName())
                .surname(client.getSurname())
                .dateOfBirth(client.getDateOfBirth())
                .email(client.getEmail())
                .build();
    }

    public ClientDto getClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Клиент с id " + id + " не найден"));
        Wallet wallet = paymentServiceClient.getWallet(id);
        return ClientDto.builder()
                .id(client.getId())
                .name(client.getName())
                .surname(client.getSurname())
                .dateOfBirth(client.getDateOfBirth())
                .email(client.getEmail())
                .wallet(wallet)
                .build();
    }

    public List<ClientDto> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(client -> {
                    return ClientDto.builder()
                            .id(client.getId())
                            .name(client.getName())
                            .surname(client.getSurname())
                            .dateOfBirth(client.getDateOfBirth())
                            .email(client.getEmail())
                            .build();
                })
                .collect(Collectors.toList());
    }
}