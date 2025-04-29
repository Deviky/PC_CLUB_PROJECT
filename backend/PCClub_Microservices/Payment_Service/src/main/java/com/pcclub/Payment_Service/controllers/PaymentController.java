package com.pcclub.Payment_Service.controllers;

import com.pcclub.Payment_Service.dto.*;
import com.pcclub.Payment_Service.models.PaymentOperation;
import com.pcclub.Payment_Service.models.Wallet;
import com.pcclub.Payment_Service.services.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Создание кошелька
    @PostMapping("/create-wallet")
    public ResponseEntity<WalletCreateResponse> createWallet(@RequestBody WalletCreateRequest request) {
        try {
            WalletCreateResponse response = paymentService.createWallet(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED); // Успешный ответ
        } catch (RuntimeException e) {
            // Обработка ошибки
            WalletCreateResponse response = new WalletCreateResponse(request.getClientId(), "Ошибка при создании кошелька: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // Ошибка с кодом 400
        }
    }

    // Пополнение кошелька
    @PostMapping("/top-up")
    public ResponseEntity<TopUpBalanceResponse> topUpWallet(@RequestBody TopUpBalanceRequest request) {
        try {
            TopUpBalanceResponse response = paymentService.topUpWallet(request);
            return new ResponseEntity<>(response, HttpStatus.OK); // Успешный ответ
        } catch (RuntimeException e) {
            // Обработка ошибки, если операция не прошла
            TopUpBalanceResponse response = new TopUpBalanceResponse(request.getClientId(), "Операция не прошла: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.PAYMENT_REQUIRED); // Ошибка с кодом 400
        }
    }

    // Оплата
    @PostMapping("/make-payment")
    public ResponseEntity<MakePaymentResponse> makePayment(@RequestBody MakePaymentRequest request) {
        try {
            MakePaymentResponse response = paymentService.makePayment(request);
            if (response.getMessage().startsWith("Ошибка при оплате")) {
                return new ResponseEntity<>(response, HttpStatus.PAYMENT_REQUIRED); // Ошибка с кодом 400
            }
            return new ResponseEntity<>(response, HttpStatus.OK); // Успешный ответ
        } catch (RuntimeException e) {
            // Обработка ошибки, если операция не прошла
            MakePaymentResponse response = new MakePaymentResponse(request.getClientId(), request.getServiceOrderId(), "Ошибка при оплате: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // Ошибка с кодом 400
        }
    }

    // Получить кошелек по clientId
    @GetMapping("/wallet/{clientId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable Long clientId) {
        Wallet wallet = paymentService.getWallet(clientId);
        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }

    // Получить все операции по clientId
    @GetMapping("/operations/{clientId}")
    public ResponseEntity<List<PaymentOperation>> getOperationsByClientId(@PathVariable Long clientId) {
        List<PaymentOperation> operations = paymentService.getOperationsByClientId(clientId);
        return new ResponseEntity<>(operations, HttpStatus.OK);
    }

    @PostMapping("/cancel-payment")
    public ResponseEntity<PaymentCancelResponse> cancelPayment(@RequestBody Long serviceOrderId) {
        try {
            PaymentCancelResponse response = paymentService.cancelPayment(serviceOrderId);
            return new ResponseEntity<>(response, HttpStatus.OK); // Успешный возврат
        } catch (RuntimeException e) {
            PaymentCancelResponse response = new PaymentCancelResponse("Ошибка при возврате: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // Ошибка
        }
    }
}
