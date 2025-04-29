package com.pcclub.Payment_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopUpBalanceRequest {
    Long clientId;
    Float amount;
    Boolean useBonus;
}
