package com.pcclub.PC_Service.dto.PC;

import com.pcclub.PC_Service.dto.ClientDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PCDeleteResponse {
    private String message;
    private List<ClientDto> clients;
}