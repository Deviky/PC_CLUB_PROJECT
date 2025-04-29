package com.PCCLub.Order_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    Long clientId;
    Long serviceOrderId;
    Float amountOfCashPayment;
    Boolean useBonus;
}
