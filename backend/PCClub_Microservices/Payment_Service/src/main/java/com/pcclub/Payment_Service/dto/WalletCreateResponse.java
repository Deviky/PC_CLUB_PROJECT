package com.pcclub.Payment_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletCreateResponse {
    Long clientId;
    String message;
}
