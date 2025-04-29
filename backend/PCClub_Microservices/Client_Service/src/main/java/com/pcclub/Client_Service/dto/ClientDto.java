package com.pcclub.Client_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientDto {
    Long id;
    String name;
    String surname;
    Date dateOfBirth;
    String email;
    Wallet wallet;
}
