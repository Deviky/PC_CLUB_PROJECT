package com.pcclub.PC_Service.services;

import com.pcclub.PC_Service.dto.PC.PCDto;
import com.pcclub.PC_Service.dto.PCGroup.PCGroupCreateRequest;
import com.pcclub.PC_Service.dto.PCGroup.PCGroupCreateResponse;
import com.pcclub.PC_Service.dto.PCGroup.PCGroupDeleteResponse;
import com.pcclub.PC_Service.dto.PCGroup.PCGroupDto;
import com.pcclub.PC_Service.dto.Reservation.ReserveDto;
import com.pcclub.PC_Service.dto.ServiceDict.ServiceDictDto;
import com.pcclub.PC_Service.models.PC;
import com.pcclub.PC_Service.models.PCGroup;
import com.pcclub.PC_Service.models.Reservation;
import com.pcclub.PC_Service.models.ServiceDict;
import com.pcclub.PC_Service.repositories.PCGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PCGroupService {

    private final PCGroupRepository pcGroupRepository;
    private final ReservationService reservationService;

    public PCGroupCreateResponse create(PCGroupCreateRequest request) {
        PCGroup group = PCGroup.builder()
                .cpu(request.getCpu())
                .gpu(request.getGpu())
                .ram(request.getRam())
                .build();

        group = pcGroupRepository.save(group);

        return PCGroupCreateResponse.builder()
                .id(group.getId())
                .message("PC-группа создана")
                .build();
    }

    public PCGroupDto get(Long id) {
        PCGroup group = pcGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PCGroup с id " + id + " не найдена"));

        // Убираем pcGroup у всех ПК
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

        // Убираем pcGroup у всех сервисов
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
                        .pcGroup(null) // чтобы не было вложенной группы
                        .build())
                .collect(Collectors.toList());

        return PCGroupDto.builder()
                .id(group.getId())
                .cpu(group.getCpu())
                .gpu(group.getGpu())
                .ram(group.getRam())
                .pcs(pcs)
                .services(services)
                .build();
    }


    public List<PCGroupDto> getAll() {
        return pcGroupRepository.findAll().stream()
                .map(group -> {
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

                    return PCGroupDto.builder()
                            .id(group.getId())
                            .cpu(group.getCpu())
                            .gpu(group.getGpu())
                            .ram(group.getRam())
                            .pcs(pcs)
                            .services(services)
                            .build();
                })
                .collect(Collectors.toList());
    }



    public PCGroupDeleteResponse delete(Long id) {
        Optional<PCGroup> pcGroupOpt = pcGroupRepository.findById(id);
        if (pcGroupOpt.isEmpty()) {
            return PCGroupDeleteResponse.builder()
                    .id(null)
                    .message("PC-группа с id " + id + " не найдена")
                    .build();
        }
        PCGroup pcGroup = pcGroupOpt.get();
        if (!pcGroup.getServices().isEmpty() || !pcGroup.getPcs().isEmpty()){
            return PCGroupDeleteResponse.builder()
                    .id(id)
                    .message("Ошибка: есть привязанные сервисы или ПК")
                    .build();
        }

        pcGroupRepository.deleteById(id);
        return PCGroupDeleteResponse.builder()
                .id(id)
                .message("PC-группа удалена")
                .build();
    }
}

