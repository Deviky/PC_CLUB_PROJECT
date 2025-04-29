package com.pcclub.PC_Service.controllers;


import com.pcclub.PC_Service.dto.PC.*;
import com.pcclub.PC_Service.dto.Reservation.ReserveDto;
import com.pcclub.PC_Service.services.PCService;
import com.pcclub.PC_Service.services.ReservationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/pc-service/pc")
@AllArgsConstructor
public class PCController {

    @Autowired
    private final PCService pcService;

    @Autowired
    private final ReservationService reservationService;

    @PostMapping("/create")
    public ResponseEntity<PCCreateResponse> create(@Valid @RequestBody PCCreateRequest pcNew) {
        PCCreateResponse response = pcService.create(pcNew);

        if (response.getId() == null) {
            // Если PCGroup не найдена, возвращаем 400 с сообщением ошибки
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Если создание ПК успешно, возвращаем 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/change-status")
    public ResponseEntity<PCChangeStatusResponse> changeStatus(@Valid @RequestBody PCChangeStatusRequest pc) {
        PCChangeStatusResponse response = pcService.changeStatus(pc);

        // Проверка на ошибку в сообщении
        if (response.getMessage().startsWith("Ошибка")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);  // Возвращаем 400 (BAD_REQUEST)
        }

        // Если все ок, возвращаем 200 (OK)
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<PCUpdateResponse> update(@RequestBody PCDto pc) {
        try {
            PCUpdateResponse response = pcService.update(pc);
            return ResponseEntity.ok(response);  // Статус 200 (OK)
        } catch (IllegalArgumentException ex) {
            // Если ПК или группа не найдены, возвращаем 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    PCUpdateResponse.builder()
                            .id(null)
                            .status(null)
                            .pcGroupId(null)
                            .build()
            );
        }
    }

    @GetMapping("/get/{pc_id}")
    public ResponseEntity<PCDto> get(@PathVariable Long pc_id) {
        try {
            PCDto pcDto = pcService.get(pc_id);
            return ResponseEntity.ok(pcDto);  // Статус 200 (OK)
        } catch (IllegalArgumentException ex) {
            // Если ПК не найден, возвращаем 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<PCDto>> getAll() {
        List<PCDto> pcDtos = pcService.getAll();
        return ResponseEntity.ok(pcDtos);  // Статус 200 (OK)
    }

    @DeleteMapping("/delete/{pcId}")
    public ResponseEntity<PCDeleteResponse> delete(@PathVariable Long pcId) {
        PCDeleteResponse response = pcService.delete(pcId);

        // Проверка на ошибку в сообщении
        if (response.getMessage().startsWith("Ошибка")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);  // Возвращаем 400 (BAD_REQUEST)
        }

        // Если ПК не найден, возвращаем 404 (NOT_FOUND)
        if (response.getMessage().contains("не найден")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Если удаление ПК успешно, возвращаем 200 (OK)
        return ResponseEntity.ok(response);
    }
    @PostMapping("/reserve")
    public ResponseEntity<String> reservePC(@RequestBody ReserveDto request) {

        // Получаем сообщение от сервиса бронирования
        String message = reservationService.reservePC(request.getPcId(), request.getReservedFrom(), request.getReservedTo());

        // Проверяем тип сообщения для возврата соответствующего ответа
        if (message.startsWith("Ошибка")) {
            // Возвращаем ошибку с сообщением и статусом 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        } else {
            // Возвращаем успешное сообщение с статусом 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        }
    }

    @PostMapping("/cancel-reserve")
    public ResponseEntity<String> cancelReservePC(@RequestBody ReserveDto request) {
        // Получаем сообщение от сервиса бронирования
        String message = reservationService.cancelReservePC(request.getPcId(), request.getReservedFrom(), request.getReservedTo());

        // Проверяем тип сообщения для возврата соответствующего ответа
        if (message.startsWith("Ошибка")) {
            // Возвращаем ошибку с сообщением и статусом 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        } else {
            // Возвращаем успешное сообщение с статусом 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        }
    }
}
