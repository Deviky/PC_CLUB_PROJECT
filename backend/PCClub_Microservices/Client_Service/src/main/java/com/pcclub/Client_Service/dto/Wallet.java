package com.pcclub.Client_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
    Long clientId;
    Float balance;
    Float bonusBalance;
}
