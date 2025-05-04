package com.PCCLub.Order_Service.services;

import com.PCCLub.Order_Service.dto.*;
import com.PCCLub.Order_Service.enums.PCStatus;
import com.PCCLub.Order_Service.models.Order;
import com.PCCLub.Order_Service.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentClientService paymentClientService;
    private final PCClientService pcClientService;
    private final ClientClientService clientService;

    @Transactional
    public OrderCreateResponse createOrder(OrderCreateRequest orderRequest) {
        // 0. Получаем информацию о сервисе
        ServiceDictDto serviceDictDto = pcClientService.getService(orderRequest.getServiceId());
        if (serviceDictDto == null) {
            return new OrderCreateResponse("Ошибка: сервис с ID " + orderRequest.getServiceId() + " не найден.");
        }

        // 1. Получаем клиента
        ClientDto clientDto;
        try {
            clientDto = clientService.getClientById(orderRequest.getClientId());
        } catch (Exception e) {
            return new OrderCreateResponse("Ошибка при получении клиента: " + e.getMessage());
        }

        // 2. Проверка возраста клиента и время сервиса
        int clientAge = Period.between(
                clientDto.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                LocalDate.now()
        ).getYears();

        if (clientAge < serviceDictDto.getMinAge() || clientAge > serviceDictDto.getMaxAge()) {
            return new OrderCreateResponse("Ошибка: возраст клиента не соответствует требованиям сервиса.");
        }

        log.debug("Время заказа: {} – {}", orderRequest.getStartDttm(), orderRequest.getEndDttm());


        ZoneId moscowZone = ZoneId.of("Europe/Moscow");

        LocalDateTime startDateTime = orderRequest.getStartDttm().toInstant()
                .atZone(moscowZone)
                .toLocalDateTime();
        
        LocalDateTime endDateTime = orderRequest.getEndDttm().toInstant()
                .atZone(moscowZone)
                .toLocalDateTime();

// Проверка на валидную продолжительность заказа
        Duration duration = Duration.between(startDateTime, endDateTime);
        if (duration.isNegative() || duration.isZero()) {
            return new OrderCreateResponse("Ошибка: длительность заказа должна быть положительной.");
        }

        LocalTime minTime = serviceDictDto.getMinTime();
        LocalTime maxTime = serviceDictDto.getMaxTime();

        log.debug("Сервис работает: {} – {}", minTime, maxTime);
        log.debug("Время заказа: {} – {}", startDateTime.toLocalTime(), endDateTime.toLocalTime());

        boolean crossesMidnight = minTime.isAfter(maxTime);
        log.debug("Сервис через полночь?: {}", crossesMidnight);

        LocalDateTime serviceStart;
        LocalDateTime serviceEnd;

        if (crossesMidnight) {
            // Сервис через полночь
            if (startDateTime.toLocalTime().isBefore(maxTime)) {
                // Если бронирование ночью (до maxTime), то сервис начался на день раньше
                serviceStart = startDateTime.toLocalDate().minusDays(1).atTime(minTime);
                serviceEnd = startDateTime.toLocalDate().atTime(maxTime);
            } else {
                // Иначе обычный интервал
                serviceStart = startDateTime.toLocalDate().atTime(minTime);
                serviceEnd = startDateTime.toLocalDate().plusDays(1).atTime(maxTime);
            }
        } else {
            // Обычный сервис без перехода через полночь
            serviceStart = startDateTime.toLocalDate().atTime(minTime);
            serviceEnd = startDateTime.toLocalDate().atTime(maxTime);
        }

        log.debug("Интервал работы сервиса после расчёта: {} – {}", serviceStart, serviceEnd);

// Проверка попадания заказа в интервал работы
        boolean isValid = !startDateTime.isBefore(serviceStart) && !endDateTime.isAfter(serviceEnd);
        log.debug("Заказ валидный?: {}", isValid);

        if (!isValid) {
            return new OrderCreateResponse("Ошибка: заказ должен находиться в пределах " +
                    minTime + " – " + maxTime + ".");
        }

        // 3. Проверка доступности ПК
        List<PCDto> pcList = serviceDictDto.getPcGroup().getPcs();
        Optional<PCDto> pcOptional = pcList.stream()
                .filter(pc -> pc.getId().equals(orderRequest.getPcId()))
                .findFirst();

        if (pcOptional.isEmpty()) {
            return new OrderCreateResponse("Ошибка: выбранный ПК не относится к группе сервиса.");
        }

        PCDto selectedPC = pcOptional.get();
        if (selectedPC.getStatus() != PCStatus.WORKED) {
            return new OrderCreateResponse("Ошибка: выбранный ПК не в рабочем состоянии.");
        }

        // 4. Проверка баланса
        float totalPrice = orderRequest.getTotalServiceCount() * serviceDictDto.getPricePerHour();
        Wallet wallet = clientDto.getWallet();

        if (Boolean.TRUE.equals(orderRequest.getUseBonus())) {
            if (wallet.getBonusBalance() < totalPrice) {
                return new OrderCreateResponse("Ошибка: недостаточно бонусных средств на счёте.");
            }
        } else {
            if (wallet.getBalance() < totalPrice) {
                return new OrderCreateResponse("Ошибка: недостаточно средств на счёте.");
            }
        }

        // 5. Попытка забронировать ПК
        ReserveDto reserveDto = ReserveDto.builder()
                .pcId(orderRequest.getPcId())
                .reservedFrom(orderRequest.getStartDttm())
                .reservedTo(orderRequest.getEndDttm())
                .build();

        String pcReservationMessage = pcClientService.reservePC(reserveDto);

        if (pcReservationMessage.startsWith("Ошибка")) {
            return new OrderCreateResponse("Ошибка при бронировании ПК: " + pcReservationMessage);
        }

        Order order = Order.builder()
                .clientId(orderRequest.getClientId())
                .serviceId(orderRequest.getServiceId())
                .pcId(orderRequest.getPcId())
                .totalServiceCount(orderRequest.getTotalServiceCount())
                .startDttm(orderRequest.getStartDttm())
                .endDttm(orderRequest.getEndDttm())
                .build();

        Order newOrder = orderRepository.save(order);


        // 6. Попытка произвести оплату
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .clientId(orderRequest.getClientId())
                .serviceOrderId(newOrder.getId())
                .amountOfCashPayment(totalPrice)
                .useBonus(orderRequest.getUseBonus())
                .build();

        PaymentResponse paymentResponse = paymentClientService.sendPayment(paymentRequest);

        if (paymentResponse.getMessage().startsWith("Ошибка")) {
            pcClientService.cancelReservation(reserveDto);
            return new OrderCreateResponse("Ошибка при оплате: " + paymentResponse.getMessage());
        }

        // 7. Сохраняем заказ

        return new OrderCreateResponse("Заказ успешно создан.");
    }

    public List<OrderDto> getOrdersByClientId(Long clientId) {
        List<Order> orders = orderRepository.findAllByClientIdOrderByStartDttm(clientId);
        return orders.stream().map(order -> OrderDto.builder()
                        .id(order.getId())
                        .clientId(order.getClientId())
                        .serviceId(order.getServiceId())
                        .pcId(order.getPcId())
                        .totalServiceCount(order.getTotalServiceCount())
                        .startDttm(order.getStartDttm())
                        .endDttm(order.getEndDttm())
                        .build())
                .collect(Collectors.toList());
    }

    public List<OrderWithClientDto> getOrdersByPCId(Long PCId) {
        List<Order> orders = orderRepository.findAllByPcId(PCId);

        // Получаем всех клиентов
        List<ClientDto> clients = clientService.getAllClients();

        // Создаем Map для быстрого поиска клиента по clientId
        Map<Long, ClientDto> clientMap = clients.stream()
                .collect(Collectors.toMap(ClientDto::getId, client -> client));

        // Маппим заказы с данными о клиентах
        return orders.stream()
                .map(order -> {
                    ClientDto client = clientMap.get(order.getClientId());
                    return OrderWithClientDto.builder()
                            .id(order.getId())
                            .clientId(order.getClientId())
                            .serviceId(order.getServiceId())
                            .pcId(order.getPcId())
                            .totalServiceCount(order.getTotalServiceCount())
                            .startDttm(order.getStartDttm())
                            .endDttm(order.getEndDttm())
                            .clientEmail(client != null ? client.getEmail() : null)  // Если клиент не найден, можно вернуть null
                            .build();
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public CancelOrderResponse cancelOrderByOrderId(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            return CancelOrderResponse.builder()
                    .message("Заказа с таким id не существует!")
                    .build();
        }

        Order order = orderOptional.get();

        // 1. Отмена бронирования ПК
        ReserveDto reserveDto = ReserveDto.builder()
                .pcId(order.getPcId())
                .reservedFrom(order.getStartDttm())
                .reservedTo(order.getEndDttm())
                .build();
        String cancelReservationMessage = pcClientService.cancelReservation(reserveDto);

        if (cancelReservationMessage.startsWith("Ошибка")) {
            return CancelOrderResponse.builder()
                    .message("Ошибка при отмене бронирования ПК: " + cancelReservationMessage)
                    .build();
        }

        // 2. Возврат денег клиенту
        PaymentCancelResponse refundResponse = paymentClientService.cancelOperation(orderId);

        if (refundResponse.getMessage().startsWith("Ошибка")) {
            // Если возврат не удался, восстанавливаем бронирование
            String restoreReservationMessage = pcClientService.reservePC(reserveDto);

            if (restoreReservationMessage.startsWith("Ошибка")) {
                return CancelOrderResponse.builder()
                        .message("Ошибка при возврате средств и восстановлении бронирования ПК: " + restoreReservationMessage)
                        .build();
            }

            return CancelOrderResponse.builder()
                    .message("Ошибка при возврате средств: " + refundResponse.getMessage() +
                            ". Бронирование ПК успешно восстановлено.")
                    .build();
        }

        // 3. Удаление заказа
        orderRepository.delete(order);

        return CancelOrderResponse.builder()
                .message("Заказ успешно отменён и средства возвращены.")
                .build();
    }
}
