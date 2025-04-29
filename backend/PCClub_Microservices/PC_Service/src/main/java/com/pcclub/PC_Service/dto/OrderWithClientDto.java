package com.pcclub.PC_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderWithClientDto {
    Long id;
    Long clientId;
    Long serviceId;
    Long pcId;
    Integer totalServiceCount;
    Date startDttm;
    Date endDttm;
    String clientEmail;
}

