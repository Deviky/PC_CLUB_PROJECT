package com.pcclub.Client_Service.controllers;

import com.pcclub.Client_Service.dto.ClientCreateRequest;
import com.pcclub.Client_Service.dto.ClientDto;
import com.pcclub.Client_Service.services.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping("/add")
    public ResponseEntity<String> addClient(@RequestBody ClientCreateRequest client) {
        String message = clientService.addClient(client);
        if (!message.startsWith("Ошибка")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Клиент с таким email уже существует");
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ClientDto>> getAllClients() {
        List<ClientDto> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/get-by-email/{email}")
    public ResponseEntity<?> getClientByEmail(@PathVariable String email) {
        try {
            ClientDto clientDto = clientService.getClientByEmail(email);
            return ResponseEntity.ok(clientDto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getClient(@PathVariable Long id) {
        try {
            ClientDto clientDto = clientService.getClient(id);
            return ResponseEntity.ok(clientDto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
