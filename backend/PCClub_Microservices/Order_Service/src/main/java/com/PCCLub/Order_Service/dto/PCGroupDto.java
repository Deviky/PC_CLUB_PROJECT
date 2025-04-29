package com.PCCLub.Order_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PCGroupDto {
    private Long id;
    private String cpu;
    private String gpu;
    private int ram;
    private List<PCDto> pcs;      // Список ПК для ServiceDictDto
    private List<ServiceDictDto> services;  // Список сервисов для PCDto
}


