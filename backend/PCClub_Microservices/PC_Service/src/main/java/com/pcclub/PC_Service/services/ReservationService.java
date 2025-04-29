package com.pcclub.PC_Service.services;

import com.pcclub.PC_Service.dto.Reservation.ReserveDto;
import com.pcclub.PC_Service.models.Reservation;
import com.pcclub.PC_Service.repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;

    // Резервируем ПК
    public String reservePC(Long pcId, Date reservedFrom, Date reservedTo) {
        // Проверка, что время окончания не меньше времени начала
        if (reservedTo.before(reservedFrom)) {
            return "Ошибка: Время окончания бронирования не может быть раньше времени начала.";
        }

        // Проверка, что время начала не прошло
        if (reservedFrom.before(new Date())) {
            return "Ошибка: Время начала бронирования уже прошло.";
        }

        // Проверка пересечений с другими бронированиями
        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(pcId, reservedFrom, reservedTo);

        if (!overlappingReservations.isEmpty()) {
            return "Ошибка: ПК уже забронирован на это время.";
        }

        // Нет пересечений, создаем новое бронирование
        Reservation reservation = Reservation.builder()
                .pcId(pcId)
                .reservedFrom(reservedFrom)
                .reservedTo(reservedTo)
                .build();

        reservationRepository.save(reservation);

        return "ПК успешно забронирован с " + reservedFrom + " до " + reservedTo;
    }

    // Отмена бронирования
    public String cancelReservePC(Long pcId, Date reservedFrom, Date reservedTo) {
        log.info("Попытка отменить бронирование ПК: {} с {} до {}", pcId, reservedFrom, reservedTo);

        Optional<Reservation> existingReservationOpt = reservationRepository.findByPcIdAndReservedFromAndReservedTo(pcId, reservedFrom, reservedTo);

        if (existingReservationOpt.isEmpty()) {
            log.error("Ошибка: Бронирование не найдено.");
            return "Ошибка: Бронирование не найдено.";
        }

        Reservation existingReservation = existingReservationOpt.get();

        // Удаляем бронирование
        reservationRepository.delete(existingReservation);

        log.info("Бронирование ПК с {} до {} успешно отменено", reservedFrom, reservedTo);
        return "Бронирование успешно отменено.";
    }


    // Получаем все бронирования для ПК
    public List<Reservation> getReservations(Long pcId) {
        return reservationRepository.findByPcId(
                pcId);
    }
}