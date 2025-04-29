package com.PCCLub.Order_Service.dto;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateRequest {
    Long clientId;
    Long serviceId;
    Long pcId;
    Integer totalServiceCount;
    Date startDttm;
    Date endDttm;
    Boolean useBonus;
}
