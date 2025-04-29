package com.pcclub.PC_Service.dto.PCGroup;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pcclub.PC_Service.dto.PC.PCDto;
import com.pcclub.PC_Service.dto.ServiceDict.ServiceDictDto;
import com.pcclub.PC_Service.models.PC;
import com.pcclub.PC_Service.models.ServiceDict;
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


