package com.pcclub.PC_Service.controllers;

import com.pcclub.PC_Service.dto.ServiceDict.*;
import com.pcclub.PC_Service.services.ServiceDictService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pc-service/service")
@AllArgsConstructor
public class ServiceDictController {
    @Autowired
    private final ServiceDictService serviceDictService;

    @PostMapping("/create")
    public ResponseEntity<ServiceDictCreateResponse> create(@Valid @RequestBody ServiceDictCreateRequest serviceNew) {
        // Вызываем сервис для создания нового сервиса
        ServiceDictCreateResponse response = serviceDictService.create(serviceNew);

        // Если сервис успешно создан, возвращаем 201 (Created)
        if (response.getId() != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        // Если группа не найдена, возвращаем 404 (Not Found)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<ServiceDictUpdateResponse> update(@Valid @RequestBody ServiceDictDto serviceNew) {
        // Вызываем сервис для обновления существующего сервиса
        ServiceDictUpdateResponse response = serviceDictService.update(serviceNew);

        // Если сервис обновлён, возвращаем 200 (OK)
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/{service_id}")
    public ResponseEntity<ServiceDictDto> get(@PathVariable Long service_id) {
        try {
            // Получаем информацию о сервисе по ID
            ServiceDictDto serviceDto = serviceDictService.get(service_id);
            return ResponseEntity.ok(serviceDto);  // Статус 200 (OK)
        } catch (IllegalArgumentException ex) {
            // Если сервис не найден, возвращаем 404 (Not Found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ServiceDictDto>> getAll() {
        // Получаем список всех сервисов
        List<ServiceDictDto> services = serviceDictService.getAll();
        return ResponseEntity.ok(services);  // Статус 200 (OK)
    }

    @DeleteMapping("/delete/{service_id}")
    public ResponseEntity<ServiceDictDeleteResponse> delete(@PathVariable Long service_id) {
        ServiceDictDeleteResponse response = serviceDictService.delete(service_id);

        // Если сервис удалён, возвращаем 200 (OK)
        if (response.getId() != null) {
            return ResponseEntity.ok(response);
        }

        // Если сервис не найден, возвращаем 404 (Not Found)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}

