package com.pcclub.PC_Service.services;

import com.pcclub.PC_Service.dto.PC.PCDto;
import com.pcclub.PC_Service.dto.PCGroup.PCGroupDto;
import com.pcclub.PC_Service.dto.Reservation.ReserveDto;
import com.pcclub.PC_Service.dto.ServiceDict.*;
import com.pcclub.PC_Service.models.PC;
import com.pcclub.PC_Service.models.PCGroup;
import com.pcclub.PC_Service.models.Reservation;
import com.pcclub.PC_Service.models.ServiceDict;
import com.pcclub.PC_Service.repositories.PCGroupRepository;
import com.pcclub.PC_Service.repositories.ServiceDictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceDictService {

    private final ServiceDictRepository serviceDictRepository;
    private final PCGroupRepository pcGroupRepository;
    private final ReservationService reservationService;

    public ServiceDictCreateResponse create(ServiceDictCreateRequest request) {
        Optional<PCGroup> groupOpt = pcGroupRepository.findById(request.getPcGroupId());

        if (groupOpt.isEmpty()) {
            return ServiceDictCreateResponse.builder()
                    .id(null)
                    .message("PCGroup с id " + request.getPcGroupId() + " не найдена")
                    .build();
        }

        ServiceDict service = ServiceDict.builder()
                .name(request.getName())
                .description(request.getDescription())
                .pricePerHour(request.getPricePerHour())
                .minAge(request.getMinAge())
                .maxAge(request.getMaxAge())
                .minTime(request.getMinTime())
                .maxTime(request.getMaxTime())
                .pcGroup(groupOpt.get())
                .build();

        service = serviceDictRepository.save(service);

        return ServiceDictCreateResponse.builder()
                .id(service.getId())
                .message("Сервис создан")
                .build();
    }

    public ServiceDictUpdateResponse update(ServiceDictDto request) {
        ServiceDict service = serviceDictRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Сервис с id " + request.getId() + " не найден"));

        PCGroup group = pcGroupRepository.findById(request.getPcGroupId())
                .orElseThrow(() -> new IllegalArgumentException("PCGroup с id " + request.getPcGroupId() + " не найдена"));

        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPricePerHour(request.getPricePerHour());
        service.setMinAge(request.getMinAge());
        service.setMaxAge(request.getMaxAge());
        service.setMinTime(request.getMinTime());
        service.setMaxTime(request.getMaxTime());
        service.setPcGroup(group);

        service = serviceDictRepository.save(service);

        return ServiceDictUpdateResponse.builder()
                .id(service.getId())
                .message("Сервис обновлён")
                .build();
    }

    public ServiceDictDto get(Long id) {
        ServiceDict service = serviceDictRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сервис с id " + id + " не найден"));

        PCGroup group = service.getPcGroup();

        // Маппим ПК и убираем у них группу
        List<PCDto> pcs = group.getPcs().stream()
                .map(pc -> {
                    // Получаем резервации для ПК
                    List<Reservation> reservations = reservationService.getReservations(pc.getId());
                    List<ReserveDto> reservationList = reservations.stream()
                            .map(reservation -> ReserveDto.builder()
                                    .reservedFrom(reservation.getReservedFrom())
                                    .reservedTo(reservation.getReservedTo())
                                    .build())
                            .collect(Collectors.toList());

                    // Создаём PCDto с резервациями
                    return PCDto.builder()
                            .id(pc.getId())
                            .status(pc.getStatus())
                            .pcGroup(null)  // Убираем информацию о группе
                            .reservationList(reservationList)  // Добавляем список резерваций
                            .build();
                })
                .collect(Collectors.toList());

        PCGroupDto groupDto = PCGroupDto.builder()
                .id(group.getId())
                .cpu(group.getCpu())
                .gpu(group.getGpu())
                .ram(group.getRam())
                .pcs(pcs)     // Список ПК
                .services(null)  // Список сервисов не включаем
                .build();

        return ServiceDictDto.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .pricePerHour(service.getPricePerHour())
                .minAge(service.getMinAge())
                .maxAge(service.getMaxAge())
                .minTime(service.getMinTime())
                .maxTime(service.getMaxTime())
                .pcGroup(groupDto)
                .build();
    }




    public List<ServiceDictDto> getAll() {
        return serviceDictRepository.findAll().stream().map(service -> {
            PCGroup group = service.getPcGroup();

            List<PCDto> pcs = group.getPcs().stream()
                    .map(pc -> {
                        // Получаем резервации для ПК
                        List<Reservation> reservations = reservationService.getReservations(pc.getId());
                        List<ReserveDto> reservationList = reservations.stream()
                                .map(reservation -> ReserveDto.builder()
                                        .reservedFrom(reservation.getReservedFrom())
                                        .reservedTo(reservation.getReservedTo())
                                        .build())
                                .collect(Collectors.toList());

                        // Создаём PCDto с резервациями
                        return PCDto.builder()
                                .id(pc.getId())
                                .status(pc.getStatus())
                                .pcGroup(null)  // Убираем информацию о группе
                                .reservationList(reservationList)  // Добавляем список резерваций
                                .build();
                    })
                    .collect(Collectors.toList());

            PCGroupDto groupDto = PCGroupDto.builder()
                    .id(group.getId())
                    .cpu(group.getCpu())
                    .gpu(group.getGpu())
                    .ram(group.getRam())
                    .pcs(pcs)
                    .services(null)
                    .build();

            return ServiceDictDto.builder()
                    .id(service.getId())
                    .name(service.getName())
                    .description(service.getDescription())
                    .pricePerHour(service.getPricePerHour())
                    .minAge(service.getMinAge())
                    .maxAge(service.getMaxAge())
                    .minTime(service.getMinTime())
                    .maxTime(service.getMaxTime())
                    .pcGroup(groupDto)
                    .build();
        }).collect(Collectors.toList());
    }



    public ServiceDictDeleteResponse delete(Long id) {
        if (!serviceDictRepository.existsById(id)) {
            return ServiceDictDeleteResponse.builder()
                    .id(null)
                    .message("Сервис с id " + id + " не найден")
                    .build();
        }

        serviceDictRepository.deleteById(id);
        return ServiceDictDeleteResponse.builder()
                .id(id)
                .message("Сервис удалён")
                .build();
    }
}
