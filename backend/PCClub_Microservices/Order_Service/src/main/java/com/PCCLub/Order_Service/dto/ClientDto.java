package com.PCCLub.Order_Service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientDto {
    Long id;
    String name;
    String surname;
    Date dateOfBirth;
    String email;
    Wallet wallet;
}
