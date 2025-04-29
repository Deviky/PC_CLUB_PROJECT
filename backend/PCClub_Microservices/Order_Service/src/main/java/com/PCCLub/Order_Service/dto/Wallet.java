package com.PCCLub.Order_Service.dto;


import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    Long clientId;
    Float balance;
    Float bonusBalance;
}
