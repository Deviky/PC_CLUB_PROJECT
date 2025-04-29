package com.pcclub.PC_Service.dto.ServiceDict;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDictDeleteResponse {
    private Long id;
    private String message;
}
