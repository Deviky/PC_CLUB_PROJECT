package com.pcclub.PC_Service.services;

import com.pcclub.PC_Service.dto.ClientDto;
import com.pcclub.PC_Service.dto.OrderWithClientDto;
import com.pcclub.PC_Service.dto.PC.*;
import com.pcclub.PC_Service.dto.PCGroup.PCGroupDto;
import com.pcclub.PC_Service.dto.Reservation.ReserveDto;
import com.pcclub.PC_Service.dto.ServiceDict.ServiceDictDto;
import com.pcclub.PC_Service.enums.PCStatus;
import com.pcclub.PC_Service.models.PC;
import com.pcclub.PC_Service.models.PCGroup;
import com.pcclub.PC_Service.models.Reservation;
import com.pcclub.PC_Service.repositories.PCGroupRepository;
import com.pcclub.PC_Service.repositories.PCRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PCService {

    private final PCRepository pcRepository;
    private final PCGroupRepository pcGroupRepository;
    private final ReservationService reservationService;
    private final OrderClientService orderClientService;

    public PCCreateResponse create(PCCreateRequest request) {
        Optional<PCGroup> pcGroupOpt = pcGroupRepository.findById(request.getPcGroupId());

        if (pcGroupOpt.isEmpty()) {
            return PCCreateResponse.builder()
                    .id(null)
                    .message("PCGroup с id " + request.getPcGroupId() + " не найдена")
                    .build();
        }

        PC pc = PC.builder()
                .status(request.getStatus())
                .pcGroup(pcGroupOpt.get())
                .build();

        pc = pcRepository.save(pc);

        return PCCreateResponse.builder()
                .id(pc.getId())
                .message("PC успешно создан")
                .build();
    }

    public PCChangeStatusResponse changeStatus(PCChangeStatusRequest request) {
        PC pc = pcRepository.findById(request.getPcId())
                .orElseThrow(() -> new IllegalArgumentException("PC с id " + request.getPcId() + " не найден"));

        if (request.getNewStatus().equals(PCStatus.NOTWORKED)){
            List<OrderWithClientDto> orders = orderClientService.getOrdersByPC(request.getPcId());
            if (!orders.isEmpty()) {
                // Собираем список клиентов, которые имеют заказы на этом ПК
                List<ClientDto> clients = orders.stream()
                        .map(order -> new ClientDto(order.getClientId(), order.getClientEmail()))
                        .collect(Collectors.toList());

                return PCChangeStatusResponse.builder()
                        .message("Ошибка: Вам необходимо убрать заказы с этим ПК у некоторых пользователей. Не забудьте их предупредить!")
                        .clients(clients)
                        .build();
            }
        }

        // Обновляем статус ПК
        pc.setStatus(request.getNewStatus());
        pc = pcRepository.save(pc);

        return PCChangeStatusResponse.builder()
                .message("Статус успешно обновлён.")
                .build();
    }

    public PCUpdateResponse update(PCDto request) {
        PC pc = pcRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("PC с id " + request.getId() + " не найден"));

        PCGroup group = pcGroupRepository.findById(request.getPcGroupId())
                .orElseThrow(() -> new IllegalArgumentException("PCGroup с id " + request.getPcGroupId() + " не найдена"));

        pc.setStatus(request.getStatus());
        pc.setPcGroup(group);
        pc = pcRepository.save(pc);

        return PCUpdateResponse.builder()
                .id(pc.getId())
                .status(pc.getStatus())
                .pcGroupId(group.getId())
                .build();
    }

    public PCDto get(Long id) {
        PC pc = pcRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PC с id " + id + " не найден"));

        PCGroup group = pc.getPcGroup();

        // Преобразуем сервисы в DTO и обнуляем pcGroup
        List<ServiceDictDto> services = group.getServices().stream()
                .map(service -> ServiceDictDto.builder()
                        .id(service.getId())
                        .name(service.getName())
                        .description(service.getDescription())
                        .pricePerHour(service.getPricePerHour())
                        .minAge(service.getMinAge())
                        .maxAge(service.getMaxAge())
                        .minTime(service.getMinTime())
                        .maxTime(service.getMaxTime())
                        .pcGroup(null)
                        .build())
                .collect(Collectors.toList());

        PCGroupDto groupDto = PCGroupDto.builder()
                .id(group.getId())
                .cpu(group.getCpu())
                .gpu(group.getGpu())
                .ram(group.getRam())
                .pcs(null) // ПК не включаем
                .services(services)
                .build();

        List<Reservation> reservations = reservationService.getReservations(id);
        List<ReserveDto> reservationList = reservations.stream()
                .map(reservation -> ReserveDto.builder()
                        .reservedFrom(reservation.getReservedFrom())
                        .reservedTo(reservation.getReservedTo())
                        .build())
                .collect(Collectors.toList());

        return PCDto.builder()
                .id(pc.getId())
                .status(pc.getStatus()) // если статус enum
                .pcGroup(groupDto)
                .reservationList(reservationList)
                .build();
    }



    public List<PCDto> getAll() {
        return pcRepository.findAll().stream().map(pc -> {
            PCGroup group = pc.getPcGroup();

            List<ServiceDictDto> services = group.getServices().stream()
                    .map(service -> ServiceDictDto.builder()
                            .id(service.getId())
                            .name(service.getName())
                            .description(service.getDescription())
                            .pricePerHour(service.getPricePerHour())
                            .minAge(service.getMinAge())
                            .maxAge(service.getMaxAge())
                            .minTime(service.getMinTime())
                            .maxTime(service.getMaxTime())
                            .pcGroup(null)
                            .build())
                    .collect(Collectors.toList());

            PCGroupDto groupDto = PCGroupDto.builder()
                    .id(group.getId())
                    .cpu(group.getCpu())
                    .gpu(group.getGpu())
                    .ram(group.getRam())
                    .pcs(null)
                    .services(services)
                    .build();

            List<Reservation> reservations = reservationService.getReservations(pc.getId());
            List<ReserveDto> reservationList = reservations.stream()
                    .map(reservation -> ReserveDto.builder()
                            .reservedFrom(reservation.getReservedFrom())
                            .reservedTo(reservation.getReservedTo())
                            .build())
                    .collect(Collectors.toList());

            return PCDto.builder()
                    .id(pc.getId())
                    .status(pc.getStatus())
                    .pcGroup(groupDto)
                    .reservationList(reservationList)
                    .build();
        }).collect(Collectors.toList());
    }



    public PCDeleteResponse delete(Long id) {
        if (!pcRepository.existsById(id)) {
            return PCDeleteResponse.builder()
                    .message("PC с id " + id + " не найден")
                    .build();
        }

        List<OrderWithClientDto> orders = orderClientService.getOrdersByPC(id);
        if (!orders.isEmpty()) {
            // Собираем список клиентов, которые имеют заказы на этом ПК
            List<ClientDto> clients = orders.stream()
                    .map(order -> new ClientDto(order.getClientId(), order.getClientEmail()))
                    .collect(Collectors.toList());

            return PCDeleteResponse.builder()
                    .message("Ошибка: Вам необходимо убрать заказы с этим ПК у некоторых пользователей. Не забудьте их предупредить!")
                    .clients(clients)
                    .build();
        }

        pcRepository.deleteById(id);
        return PCDeleteResponse.builder()
                .message("PC удалён")
                .build();
    }
}


