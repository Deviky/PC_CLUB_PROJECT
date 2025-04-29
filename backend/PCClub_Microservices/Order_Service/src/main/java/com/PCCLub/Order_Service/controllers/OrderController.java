package com.PCCLub.Order_Service.controllers;


import com.PCCLub.Order_Service.dto.*;
import com.PCCLub.Order_Service.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderCreateResponse> createOrder(@RequestBody OrderCreateRequest request) {
        OrderCreateResponse response = orderService.createOrder(request);

        if (response.getMessage().startsWith("Ошибка")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/get-by-client/{clientId}")
    public ResponseEntity<List<OrderDto>> getOrdersByClient(@PathVariable Long clientId) {
        List<OrderDto> orders = orderService.getOrdersByClientId(clientId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/get-by-pc/{pcId}")
    public ResponseEntity<List<OrderWithClientDto>> getOrdersByPC(@PathVariable Long pcId) {
        List<OrderWithClientDto> orders = orderService.getOrdersByPCId(pcId);
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<CancelOrderResponse> cancelOrder(@PathVariable Long orderId) {
        CancelOrderResponse response = orderService.cancelOrderByOrderId(orderId);

        if (response.getMessage().startsWith("Ошибка")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.ok(response);
    }

}
