package com.pcclub.Payment_Service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wallet {
    @Column(name="client_id")
    @Id
    Long clientId;
    Float balance;
    @Column(name="bonus_balance")
    Float bonusBalance;
}
