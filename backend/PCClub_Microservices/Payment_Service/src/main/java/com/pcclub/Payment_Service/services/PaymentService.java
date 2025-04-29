package com.pcclub.Payment_Service.services;

import com.pcclub.Payment_Service.dto.*;
import com.pcclub.Payment_Service.enums.PaymentStatus;
import com.pcclub.Payment_Service.models.PaymentOperation;
import com.pcclub.Payment_Service.models.Wallet;
import com.pcclub.Payment_Service.repositories.WalletRepository;
import com.pcclub.Payment_Service.repositories.PaymentOperationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class PaymentService {

    private final WalletRepository walletRepository;
    private final PaymentOperationRepository paymentOperationRepository;

    // Создание кошелька
    public WalletCreateResponse createWallet(WalletCreateRequest newWallet) {
        if (walletRepository.existsById(newWallet.getClientId())) {
            return new WalletCreateResponse(newWallet.getClientId(), "Кошелек уже существует.");
        }

        Wallet wallet = Wallet.builder()
                .clientId(newWallet.getClientId())
                .balance(0f)
                .bonusBalance(0f)
                .build();

        if (newWallet.getBonusBalance() > 0) {
            try {
                // Создаём операцию пополнения бонусами
                PaymentOperation operation = PaymentOperation.builder()
                        .clientId(wallet.getClientId())
                        .serviceOrderId(null)
                        .amountOfCashPayment(newWallet.getBonusBalance())
                        .useBonus(true)
                        .build();

                addOperation(operation, false); // Ожидаем результат выполнения операции

                // Если операция успешна, начисляем бонус
                wallet.setBonusBalance(newWallet.getBonusBalance());
                walletRepository.save(wallet); // Обновляем кошелек после успешной операции

                return new WalletCreateResponse(wallet.getClientId(), "Вам начислен бонус.");
            } catch (RuntimeException e) {
                // Операция не прошла, откатим изменения
                return new WalletCreateResponse(wallet.getClientId(), "Операция не прошла.");
            }
        }
        else{
            walletRepository.save(wallet);
        }

        return new WalletCreateResponse(wallet.getClientId(), "Кошелек успешно создан.");
    }

    // Пополнение кошелька
    public TopUpBalanceResponse topUpWallet(TopUpBalanceRequest request) {
        Wallet wallet = walletRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Кошелек не найден"));

        // Создаём операцию пополнения
        try {
            PaymentOperation operation = PaymentOperation.builder()
                    .clientId(request.getClientId())
                    .serviceOrderId(null)
                    .amountOfCashPayment(request.getAmount())
                    .useBonus(request.getUseBonus())
                    .build();

            addOperation(operation, true); // Ожидаем результат выполнения операции

            // Если операция успешна, пополняем кошелек
            if (request.getUseBonus()) {
                wallet.setBonusBalance(wallet.getBonusBalance() + request.getAmount());
            } else {
                wallet.setBalance(wallet.getBalance() + request.getAmount());
            }

            walletRepository.save(wallet); // Обновляем кошелек после успешной операции

            return new TopUpBalanceResponse(request.getClientId(), "Кошелек пополнен.");
        } catch (RuntimeException e) {
            // Операция не прошла, откатим транзакцию
            throw new RuntimeException("Ошибка при пополнении кошелька. Операция не выполнена.");
        }
    }

    // Оплата
    public MakePaymentResponse makePayment(MakePaymentRequest request) {
        Wallet wallet = walletRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Кошелек не найден"));

        // Создаём операцию оплаты
        try {
            Float cashBack = 0f;
            if (request.getUseBonus()) {
                if (wallet.getBonusBalance() < request.getAmountOfCashPayment()) {
                    return new MakePaymentResponse(request.getClientId(), request.getServiceOrderId(), "Ошибка при оплате: недостаточно бонусов.");
                }
                wallet.setBonusBalance(wallet.getBonusBalance() - request.getAmountOfCashPayment());
            } else {
                if (wallet.getBalance() < request.getAmountOfCashPayment()) {
                    return new MakePaymentResponse(request.getClientId(), request.getServiceOrderId(), "Ошибка при оплате: недостаточно средств.");
                }
                wallet.setBalance(wallet.getBalance() - request.getAmountOfCashPayment());
                cashBack = request.getAmountOfCashPayment() * 0.05f;
                wallet.setBonusBalance(wallet.getBonusBalance() + cashBack);
            }

            PaymentOperation operation = PaymentOperation.builder()
                    .clientId(request.getClientId())
                    .serviceOrderId(request.getServiceOrderId())
                    .amountOfCashPayment(request.getAmountOfCashPayment())
                    .useBonus(request.getUseBonus())
                    .build();

            addOperation(operation, false); // Ожидаем результат выполнения операции

            walletRepository.save(wallet); // Обновляем кошелек после успешной операции
            return new MakePaymentResponse(request.getClientId(), request.getServiceOrderId(), "Оплата произведена.");
        } catch (RuntimeException e) {
            // Операция не прошла, откатим транзакцию
            throw new RuntimeException("Ошибка при оплате. Операция не выполнена.");
        }
    }

    // Получить кошелек
    public Wallet getWallet(Long clientId) {
        return walletRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Кошелек не найден"));
    }

    // Получить все операции
    public List<PaymentOperation> getOperationsByClientId(Long clientId) {
        return paymentOperationRepository.findAllByClientIdOrderByOperationDttmDesc(clientId);
    }

    private void addOperation(PaymentOperation operation, Boolean isReplinishment) {
        operation.setPaymentStatus(PaymentStatus.IN_PROGRESS);
        operation.setOperationDttm(new Date());
        PaymentOperation saved = paymentOperationRepository.save(operation);

        // Выполнение операции синхронно
        boolean isSuccess = new Random().nextBoolean();

        if (isSuccess || operation.getUseBonus() || !isReplinishment) {
            saved.setPaymentStatus(PaymentStatus.SUCCESS);
            paymentOperationRepository.save(saved);
        } else {
            saved.setPaymentStatus(PaymentStatus.FAILED);
            paymentOperationRepository.save(saved);
            throw new RuntimeException("Операция не выполнена успешно, статус: " + PaymentStatus.FAILED);
        }
    }


    public PaymentCancelResponse cancelPayment(Long serviceOrderId) {
        PaymentOperation operation = paymentOperationRepository.findByServiceOrderId(serviceOrderId)
                .orElseThrow(() -> new RuntimeException("Операция по заказу не найдена"));

        // Проверяем, что операция уже завершена (статус SUCCESS) перед отменой
        if (operation.getPaymentStatus() != PaymentStatus.SUCCESS) {
            return new PaymentCancelResponse("Нельзя вернуть средства: операция ещё не завершена или уже отменена.");
        }

        // Проверка, чтобы не было повторного возврата
        if (operation.getPaymentStatus() == PaymentStatus.RETURNED) {
            return new PaymentCancelResponse("Средства уже были возвращены для этой операции.");
        }

        Wallet wallet = walletRepository.findById(operation.getClientId())
                .orElseThrow(() -> new RuntimeException("Кошелек клиента не найден"));

        // Возврат денег или бонусов
        if (operation.getUseBonus()) {
            wallet.setBonusBalance(wallet.getBonusBalance() + operation.getAmountOfCashPayment());
        } else {
            wallet.setBalance(wallet.getBalance() + operation.getAmountOfCashPayment());
        }

        // Обновляем статус операции на RETURNED
        operation.setPaymentStatus(PaymentStatus.RETURNED);

        // Сохраняем изменения в базе данных
        walletRepository.save(wallet);
        paymentOperationRepository.save(operation);

        return new PaymentCancelResponse("Средства успешно возвращены.");
    }
}